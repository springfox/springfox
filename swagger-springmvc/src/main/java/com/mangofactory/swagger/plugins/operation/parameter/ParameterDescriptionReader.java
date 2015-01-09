package com.mangofactory.swagger.plugins.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterDescriptionReader")
public class ParameterDescriptionReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
    String description = methodParameter.getParameterName();
    if (null != apiParam && hasText(apiParam.value())) {
      description = apiParam.value();
    }
    context.parameterBuilder().description(description);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
