package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.models.Annotations;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
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
import static com.mangofactory.swagger.core.ModelUtils.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class DefaultResponseMessageReader extends SwaggerResponseMessageReader {

  @Override
  protected Collection<ResponseMessage> read(SwaggerGlobalSettings swaggerGlobalSettings,
                                             RequestMethod currentHttpMethod, HandlerMethod handlerMethod) {
    List<ResponseMessage> responseMessages = globalResponseMessages(swaggerGlobalSettings, currentHttpMethod);
    Map<Integer, ResponseMessage> byStatusCode = newHashMap(uniqueIndex(responseMessages, byStatusCode()));

    applyAnnotatedOverrides(swaggerGlobalSettings, handlerMethod, byStatusCode);

    applyReturnTypeOverride(swaggerGlobalSettings, handlerMethod, byStatusCode);

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

  private void applyReturnTypeOverride(SwaggerGlobalSettings swaggerGlobalSettings, HandlerMethod handlerMethod,
      Map<Integer, ResponseMessage> byStatusCode) {

    ResolvedType returnType = handlerReturnType(swaggerGlobalSettings.getTypeResolver(), handlerMethod);
    returnType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(returnType);
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
    responseWithModel = new ResponseMessage(httpStatusCode, message, simpleName);
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

  private void applyAnnotatedOverrides(SwaggerGlobalSettings swaggerGlobalSettings, HandlerMethod handlerMethod,
                                       Map<Integer, ResponseMessage> byStatusCode) {
    Optional<ApiResponses> apiResponsesOptional = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
    if (apiResponsesOptional.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        String overrideTypeName = overrideTypeName(swaggerGlobalSettings, apiResponse);
        ResponseMessage responseMessage = byStatusCode.get(apiResponse.code());
        if (null == responseMessage) {
          byStatusCode.put(apiResponse.code(),
                  new ResponseMessage(apiResponse.code(), apiResponse.message(), overrideTypeName));
        } else {
          String responseModel = responseMessage.getResponseModel();
          if (!isNullOrEmpty(overrideTypeName)) {
            responseModel = overrideTypeName;
          }
          byStatusCode.put(apiResponse.code(),
                  new ResponseMessage(apiResponse.code(), coalese(apiResponse.message(), responseMessage.getMessage()),
                          responseModel));
        }
      }
    }
  }

  private String overrideTypeName(SwaggerGlobalSettings swaggerGlobalSettings, ApiResponse apiResponse) {
    if (apiResponse.response() != null) {
      return typeName(swaggerGlobalSettings.getTypeResolver().resolve(apiResponse.response()));
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

  private List<ResponseMessage> globalResponseMessages(SwaggerGlobalSettings swaggerGlobalSettings,
      RequestMethod currentHttpMethod) {
    List<ResponseMessage> responseMessages = newArrayList();
    Map<RequestMethod, List<ResponseMessage>> globalResponseMessages
            = swaggerGlobalSettings.getGlobalResponseMessages();

    if (null != globalResponseMessages) {
      responseMessages.addAll(
              Optional.fromNullable(globalResponseMessages.get(currentHttpMethod))
                      .or(new ArrayList<ResponseMessage>()));
    }
    return responseMessages;
  }

}
