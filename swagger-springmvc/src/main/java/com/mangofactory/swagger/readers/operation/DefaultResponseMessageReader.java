package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.models.Response;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.mangofactory.swagger.core.ModelUtils.handlerReturnType;
import static com.mangofactory.swagger.models.ResolvedTypes.typeName;

//import com.wordnik.swagger.model.ResponseMessage;
//import static com.mangofactory.swagger.ScalaUtils.*;

public class DefaultResponseMessageReader extends SwaggerResponseMessageReader {

  @Override
  protected Collection<Response> read(SwaggerGlobalSettings swaggerGlobalSettings,
                                             RequestMethod currentHttpMethod, HandlerMethod handlerMethod) {
    List<Response> responseMessages = globalResponseMessages(swaggerGlobalSettings, currentHttpMethod);
    Map<Integer, Response> byStatusCode = newHashMap(uniqueIndex(responseMessages, byStatusCode()));

    applyAnnotatedOverrides(swaggerGlobalSettings, handlerMethod, byStatusCode);

    applyReturnTypeOverride(swaggerGlobalSettings, handlerMethod, byStatusCode);

    return byStatusCode.values();
  }

  private void applyReturnTypeOverride(SwaggerGlobalSettings swaggerGlobalSettings, HandlerMethod handlerMethod,
      Map<Integer, Response> byStatusCode) {

    ResolvedType returnType = handlerReturnType(swaggerGlobalSettings.getTypeResolver(), handlerMethod);
    returnType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(returnType);
    if (!Void.class.equals(returnType.getErasedType()) && !Void.TYPE.equals(returnType.getErasedType())) {
//      ResponseMessage responseMessage = byStatusCode.get(200);
      String message = null;
//      if (responseMessage != null) {
//        message = coalese(responseMessage.message(), HttpStatus.OK.getReasonPhrase());
//      }
//      String simpleName = ResolvedTypes.typeName(returnType);
//      ResponseMessage responseWithModel = new ResponseMessage(200, message, toOption(simpleName));
//      byStatusCode.put(200, responseWithModel);
    }
  }

  private void applyAnnotatedOverrides(SwaggerGlobalSettings swaggerGlobalSettings, HandlerMethod handlerMethod,
                                       Map<Integer, Response> byStatusCode) {
//    Optional<ApiResponses> apiResponsesOptional = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
//    if (apiResponsesOptional.isPresent()) {
//      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
//      for (ApiResponse apiResponse : apiResponseAnnotations) {
//        String overrideTypeName = overrideTypeName(swaggerGlobalSettings, apiResponse);
//        ResponseMessage responseMessage = byStatusCode.get(apiResponse.code());
//        if (null == responseMessage) {
//          byStatusCode.put(apiResponse.code(),
//                  new ResponseMessage(apiResponse.code(), apiResponse.message(), toOption(overrideTypeName)));
//        } else {
//          Option<String> responseModel = responseMessage.responseModel();
//          if (!isNullOrEmpty(overrideTypeName)) {
//            responseModel = toOption(overrideTypeName);
//          }
//          byStatusCode.put(apiResponse.code(),
//                  new ResponseMessage(apiResponse.code(), coalese(apiResponse.message(), responseMessage.message()),
//                          responseModel));
//        }
//      }
//    }
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

  private Function<? super Response, Integer> byStatusCode() {
    return new Function<Response, Integer>() {
      @Override
      public Integer apply(Response input) {
//        return input.code();
        return 200;
      }
    };
  }

  private List<Response> globalResponseMessages(SwaggerGlobalSettings swaggerGlobalSettings, RequestMethod
          currentHttpMethod) {
    List<Response> responseMessages = newArrayList();
//    Map<RequestMethod, List<ResponseMessage>> globalResponseMessages
//            = swaggerGlobalSettings.getGlobalResponseMessages();
//
//    if (null != globalResponseMessages) {
//      responseMessages = globalResponseMessages.get(currentHttpMethod);
//    }
    return responseMessages;
  }

}
