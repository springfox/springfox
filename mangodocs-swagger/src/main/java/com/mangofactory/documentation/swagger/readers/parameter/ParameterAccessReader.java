package com.mangofactory.documentation.swagger.readers.parameter;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import static com.google.common.base.Strings.*;

@Component("swaggerParameterAccessReader")
public class ParameterAccessReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
    if (apiParam != null && !isNullOrEmpty(apiParam.access())) {
      String access = apiParam.access();
      context.parameterBuilder().parameterAccess(access);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
