package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;

public class OperationSummaryReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      ApiOperation apiOperationAnnotation = context.getApiOperationAnnotation();

      String summary = handlerMethod.getMethod().getName();
      if (null != apiOperationAnnotation && !StringUtils.isBlank(apiOperationAnnotation.value())) {
         summary = apiOperationAnnotation.value();
      }
      context.put("summary", summary);
   }


}
