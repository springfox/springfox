package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class OperationDeprecatedReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
     boolean isDeprecated = context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null;
     context.put("deprecated", String.valueOf(isDeprecated));
   }
}
