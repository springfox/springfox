package com.mangofactory.documentation.spring.web.readers.parameter;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
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
