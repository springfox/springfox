package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class OperationResponseMessageReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
//      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
//      swaggerGlobalSettings.getGlobalResponseMessages();
   }
}
