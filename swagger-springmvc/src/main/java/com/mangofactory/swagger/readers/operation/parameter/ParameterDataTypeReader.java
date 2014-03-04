package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.ModelUtils;
import com.mangofactory.swagger.models.Types;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      ResolvedMethodParameter methodParameter = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
      SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
      ResolvedType parameterType = methodParameter.getResolvedParameterType();
      String swaggerDataType = ModelUtils.getModelName(swaggerGlobalSettings.getTypeResolver(), parameterType);

      if (null != parameterType) {
         swaggerDataType = Types.typeNameFor(parameterType.getErasedType());
      }
      if (null == swaggerDataType) {
         swaggerDataType = ModelUtils.getModelName(swaggerGlobalSettings.getTypeResolver(), parameterType);
      }
      context.put("dataType", swaggerDataType);
   }

}
