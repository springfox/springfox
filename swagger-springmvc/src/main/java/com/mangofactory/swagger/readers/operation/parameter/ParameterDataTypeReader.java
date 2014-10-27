package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.ModelUtils;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {
  private static final Logger log = LoggerFactory.getLogger(ParameterDataTypeReader.class);
  private static final Map<Type, String> dataTypes = ImmutableMap.<Type, String>builder()
          .put(Long.TYPE, "integer")
          .put(Long.class, "integer")

          .put(Integer.TYPE, "integer")
          .put(Integer.class, "integer")

          .put(Short.TYPE, "integer")
          .put(Short.class, "integer")

          .put(BigInteger.class, "integer")//64b

          .put(BigDecimal.class, "number")

          .put(Double.TYPE, "number")
          .put(Double.class, "number")

          .put(Float.TYPE, "number")
          .put(Float.class, "number")

          .put(String.class, "string")
          .put(Character.TYPE, "string")

          .put(Byte.TYPE, "string")
          .put(Byte.class, "string")

          .put(Boolean.TYPE, "boolean")
          .put(Boolean.class, "boolean")

          .put(Date.class, "string")

          .build();

  @Override
  public void execute(RequestMappingContext context) {
    ResolvedMethodParameter methodParameter = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(parameterType);

    Class<?> erasedType = parameterType.getErasedType();
    log.debug("Resolving methodParameter:[{}] erasedType:[{}] parameterType:[{}]", methodParameter, erasedType,
            parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(erasedType)) {
      context.put("dataType", "file");
    } else {
      String dataType = dataTypes.get(erasedType);
      if (null != dataType) {
        context.put("dataType", dataType);
      } else {
        context.put("dataType", ModelUtils.getResponseClassName(parameterType));
      }
    }
  }
}
