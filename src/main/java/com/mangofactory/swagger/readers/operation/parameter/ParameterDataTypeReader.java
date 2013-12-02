package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.core.MethodParameter;

import java.util.Map;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {

      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      Map<Class, String> parameterDataTypes = (Map<Class, String>) context.get("parameterDataTypes");
      ResolvedType resolvedType = new TypeResolver().resolve(methodParameter.getParameterType());
      String swaggerDataType = parameterDataTypes.get(resolvedType.getErasedType());


      context.put("dataType", null == swaggerDataType ? "string" : swaggerDataType);
      context.put("format", "int64");
      context.put("paramAccess", "");
   }

}
