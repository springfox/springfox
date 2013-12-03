package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.core.MethodParameter;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
      ResolvedType resolvedType = new TypeResolver().resolve(methodParameter.getParameterType());
      String swaggerDataType = swaggerGlobalSettings.getParameterDataTypes().get(resolvedType.getErasedType());
      context.put("dataType", null == swaggerDataType ? "string" : swaggerDataType);
   }

}
