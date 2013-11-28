package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class OperationDeprecatedReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      context.put("deprecated", context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null);
   }
}
