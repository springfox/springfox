package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.models.Annotations;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import scala.Option;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.ScalaUtils.*;
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

    return byStatusCode.values();
  }

  private void applyReturnTypeOverride(SwaggerGlobalSettings swaggerGlobalSettings, HandlerMethod handlerMethod,
      Map<Integer, ResponseMessage> byStatusCode) {

    ResolvedType returnType = handlerReturnType(swaggerGlobalSettings.getTypeResolver(), handlerMethod);
    returnType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(returnType);
    if (!Void.class.equals(returnType.getErasedType()) && !Void.TYPE.equals(returnType.getErasedType())) {
      ResponseMessage responseMessage = byStatusCode.get(200);
      String message = null;
      if (responseMessage != null) {
        message = coalese(responseMessage.message(), HttpStatus.OK.getReasonPhrase());
      }
      String simpleName = ResolvedTypes.typeName(returnType);
      ResponseMessage responseWithModel = new ResponseMessage(200, message, toOption(simpleName));
      byStatusCode.put(200, responseWithModel);
    }
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
                  new ResponseMessage(apiResponse.code(), apiResponse.message(), toOption(overrideTypeName)));
        } else {
          Option<String> responseModel = responseMessage.responseModel();
          if (!isNullOrEmpty(overrideTypeName)) {
            responseModel = toOption(overrideTypeName);
          }
          byStatusCode.put(apiResponse.code(),
                  new ResponseMessage(apiResponse.code(), coalese(apiResponse.message(), responseMessage.message()),
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
        return input.code();
      }
    };
  }

  private List<ResponseMessage> globalResponseMessages(SwaggerGlobalSettings swaggerGlobalSettings, RequestMethod
          currentHttpMethod) {
    List<ResponseMessage> responseMessages = newArrayList();
    Map<RequestMethod, List<ResponseMessage>> globalResponseMessages
            = swaggerGlobalSettings.getGlobalResponseMessages();

    if (null != globalResponseMessages) {
      responseMessages = globalResponseMessages.get(currentHttpMethod);
    }
    return responseMessages;
  }

}
