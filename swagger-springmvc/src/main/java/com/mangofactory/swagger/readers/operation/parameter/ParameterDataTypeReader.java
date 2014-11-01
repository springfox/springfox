package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.ModelUtils;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {
  private static final Logger log = LoggerFactory.getLogger(ParameterDataTypeReader.class);

  @Override
  public void execute(RequestMappingContext context) {
    ResolvedMethodParameter methodParameter = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(parameterType);

    Class<?> erasedType = parameterType.getErasedType();
    log.debug("Resolving methodParameter:[{}] erasedType:[{}] parameterType:[{}]",
            methodParameter.getMethodParameter().getParameterName(),
            erasedType,
            parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(erasedType)) {
      context.put("dataType", "file");
    } else {
      context.put("dataType", ModelUtils.getResponseClassName(parameterType));
    }
  }
}
