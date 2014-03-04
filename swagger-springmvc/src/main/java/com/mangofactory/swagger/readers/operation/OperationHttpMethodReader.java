package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

public class OperationHttpMethodReader implements Command<RequestMappingContext> {
   private static final Logger log = LoggerFactory.getLogger(OperationHttpMethodReader.class);

   @Override
   public void execute(RequestMappingContext context) {
      RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
      HandlerMethod handlerMethod = context.getHandlerMethod();

      String requestMethod = currentHttpMethod.toString();
      ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

      if (apiOperationAnnotation != null && !StringUtils.isBlank(apiOperationAnnotation.httpMethod())) {
         String apiMethod = apiOperationAnnotation.httpMethod();
         try {
            RequestMethod.valueOf(apiMethod);
            requestMethod = apiMethod;
         } catch (IllegalArgumentException e) {
            log.error("Invalid http method: " + apiMethod + "Valid ones are [" + RequestMethod.values() + "]", e);
         }
      }
      context.put("httpRequestMethod", requestMethod);
   }
}
