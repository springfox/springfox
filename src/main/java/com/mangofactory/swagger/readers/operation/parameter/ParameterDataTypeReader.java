package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.ModelUtils;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.core.MethodParameter;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
      Class<?> parameterType = methodParameter.getParameterType();
      ResolvedType resolvedType = null;
      String swaggerDataType = ModelUtils.getModelName(parameterType);

      if (null != parameterType) {
         resolvedType = new TypeResolver().resolve(parameterType);
         swaggerDataType = swaggerGlobalSettings.getParameterDataTypes().get(resolvedType.getErasedType());
      }
      if (null == swaggerDataType) {
         /**
          * TODO - the model itself is not being generated - ui only works if that
          * sane model is response type in another method
          */
         swaggerDataType = ModelUtils.getModelName(parameterType);
      }
      context.put("dataType", swaggerDataType);
   }

}
