package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class ParameterMultiplesReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {

    MethodParameter methodParameter = context.methodParameter();
    Boolean allowMultiple;
    Class<?> parameterType = methodParameter.getParameterType();
    if (parameterType != null) {
      allowMultiple = parameterType.isArray()
              || Iterable.class.isAssignableFrom(parameterType);
      context.parameterBuilder().allowMultiple(allowMultiple);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
