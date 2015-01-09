package com.mangofactory.swagger.plugins.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterContext;
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
