package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.schema.Annotations;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.ResponseMessageBuilder;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.swagger.core.ModelUtils.*;

@Component
public class DefaultResponseMessageReader extends SwaggerResponseMessageReader {

  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public DefaultResponseMessageReader(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  protected Collection<ResponseMessage> read(RequestMappingContext context, RequestMethod currentHttpMethod,
                                             HandlerMethod handlerMethod) {
    List<ResponseMessage> responseMessages = globalResponseMessages(context, currentHttpMethod);
    Map<Integer, ResponseMessage> byStatusCode = newHashMap(uniqueIndex(responseMessages, byStatusCode()));

    applyAnnotatedOverrides(handlerMethod, byStatusCode);

    applyReturnTypeOverride(handlerMethod, byStatusCode);

    return Ordering.from(responseMessageComparer()).sortedCopy(byStatusCode.values());
  }

  private Comparator<ResponseMessage> responseMessageComparer() {
    return new Comparator<ResponseMessage>() {
      @Override
      public int compare(ResponseMessage first, ResponseMessage second) {
        return Ints.compare(first.getCode(), second.getCode());
      }
    };
  }

  private void applyReturnTypeOverride(HandlerMethod handlerMethod,
      Map<Integer, ResponseMessage> byStatusCode) {

    ResolvedType returnType = handlerReturnType(typeResolver, handlerMethod);
    returnType = alternateTypeProvider.alternateFor(returnType);
    int httpStatusCode = httpStatusCode(handlerMethod);
    ResponseMessage responseMessage = byStatusCode.get(httpStatusCode);
    String message = null;
    if (responseMessage != null) {
      message = coalese(responseMessage.getMessage(), HttpStatus.OK.getReasonPhrase());
    }
    ResponseMessage responseWithModel;
    String simpleName = null;
    if (!Void.class.equals(returnType.getErasedType()) && !Void.TYPE.equals(returnType.getErasedType())) {
      simpleName = ResolvedTypes.typeName(returnType);
    }
    responseWithModel = new ResponseMessageBuilder().code(httpStatusCode).message(message).responseModel(simpleName)
            .build();
    byStatusCode.put(httpStatusCode, responseWithModel);
  }

  private int httpStatusCode(HandlerMethod handlerMethod) {
    Optional<ResponseStatus> responseStatus = Optional.fromNullable(AnnotationUtils.getAnnotation(handlerMethod
            .getMethod(), ResponseStatus.class));
    int httpStatusCode = 200;
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

  private void applyAnnotatedOverrides(HandlerMethod handlerMethod,
                                       Map<Integer, ResponseMessage> byStatusCode) {
    Optional<ApiResponses> apiResponsesOptional = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
    if (apiResponsesOptional.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        String overrideTypeName = overrideTypeName(apiResponse);
        ResponseMessage responseMessage = byStatusCode.get(apiResponse.code());
        if (null == responseMessage) {
          byStatusCode.put(apiResponse.code(),
                  new ResponseMessageBuilder().code(apiResponse.code()).message(apiResponse.message()).responseModel
                          (overrideTypeName).build());
        } else {
          String responseModel = responseMessage.getResponseModel();
          if (!isNullOrEmpty(overrideTypeName)) {
            responseModel = overrideTypeName;
          }
          byStatusCode.put(apiResponse.code(),
                  new ResponseMessageBuilder().code(apiResponse.code()).message(coalese(apiResponse.message(),
                          responseMessage.getMessage())).responseModel(responseModel).build());
        }
      }
    }
  }

  private String overrideTypeName(ApiResponse apiResponse) {
    if (apiResponse.response() != null) {
      return typeName(typeResolver.resolve(apiResponse.response()));
    }
    return "";
  }

  private String coalese(String overrideMessage, String defaultMessage) {
    if (isNullOrEmpty(overrideMessage)) {
      return defaultMessage;
    }
    return overrideMessage;
  }

  private Function<? super ResponseMessage, Integer> byStatusCode() {
    return new Function<ResponseMessage, Integer>() {
      @Override
      public Integer apply(ResponseMessage input) {
        return input.getCode();
      }
    };
  }

  private List<ResponseMessage> globalResponseMessages(RequestMappingContext context, RequestMethod currentHttpMethod) {
    List<ResponseMessage> responseMessages = newArrayList();
    Map<RequestMethod, List<ResponseMessage>> globalResponseMessages
            = context.getDocumentationContext().getGlobalResponseMessages();

    if (null != globalResponseMessages) {
      responseMessages.addAll(
              Optional.fromNullable(globalResponseMessages.get(currentHttpMethod))
                      .or(new ArrayList<ResponseMessage>()));
    }
    return responseMessages;
  }

}
