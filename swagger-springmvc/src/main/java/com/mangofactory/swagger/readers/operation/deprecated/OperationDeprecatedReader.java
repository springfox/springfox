package com.mangofactory.swagger.readers.operation.deprecated;

import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class OperationDeprecatedReader implements RequestMappingReader {
   @Override
   public void execute(RequestMappingContext context) {
     boolean isDeprecated = context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null;
     context.put("deprecated", String.valueOf(isDeprecated));
   }
}
