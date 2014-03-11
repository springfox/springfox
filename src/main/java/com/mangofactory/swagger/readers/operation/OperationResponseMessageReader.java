package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.ScalaUtils.toOption;
import static com.mangofactory.swagger.core.ModelUtils.getHandlerReturnType;

public class OperationResponseMessageReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
      RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
      HandlerMethod handlerMethod = context.getHandlerMethod();
      List<ResponseMessage> responseMessages = new ArrayList();
      ApiResponses apiResponses = getAnnotatedResponseMessages(handlerMethod.getMethod());

      if (null != apiResponses) {
         ApiResponse[] apiResponseAnnotations = apiResponses.value();
         for (ApiResponse apiResponse : apiResponseAnnotations) {
            responseMessages.add(new ResponseMessage(apiResponse.code(), apiResponse.message(), toOption(null)));
         }
      } else {
         Map<RequestMethod, List<ResponseMessage>> globalResponseMessages = swaggerGlobalSettings.getGlobalResponseMessages();
         if (null != globalResponseMessages) {
            responseMessages = globalResponseMessages.get(currentHttpMethod);
         }
         Class<?> returnType = getHandlerReturnType(handlerMethod);
         if (Void.class != returnType) {
            String simpleName = returnType.getSimpleName();
            ResponseMessage responseWithModel = new ResponseMessage(200, HttpStatus.OK.getReasonPhrase(), toOption(simpleName));
            safelyRemoveHttpOkResponse(responseMessages);
            responseMessages.add(responseWithModel);
         }
      }
      context.put("responseMessages", responseMessages);
   }

   private ApiResponses getAnnotatedResponseMessages(Method method) {
      Annotation[] methodAnnotations = method.getAnnotations();
      if (null != methodAnnotations) {
         for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof ApiResponses) {
               return (ApiResponses) annotation;
            }
         }
      }
      return null;
   }

   private void safelyRemoveHttpOkResponse(List<ResponseMessage> responseMessages) {
      //Safely remove using iterator
      if (null != responseMessages && responseMessages.size() > 0) {
         Iterator<ResponseMessage> i = responseMessages.iterator();
         while (i.hasNext()) {
            ResponseMessage msg = i.next();
            if (msg.code() == 200) {
               i.remove();
               break;
            }
         }
      }
   }
}
