package com.mangofactory.documentation.spring.web.readers.parameter;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.Annotation;

@Component
public class ParameterDefaultReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    String defaultValue = findAnnotatedDefaultValue(methodParameter);
    boolean isSkip = ValueConstants.DEFAULT_NONE.equals(defaultValue);
    if (!isSkip) {
      context.parameterBuilder().defaultValue(defaultValue);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findAnnotatedDefaultValue(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    for (Annotation annotation : methodAnnotations) {
      if (annotation instanceof RequestParam) {
        return ((RequestParam) annotation).defaultValue();
      } else if (annotation instanceof RequestHeader) {
        return ((RequestHeader) annotation).defaultValue();
      }
    }
    return null;
  }
}
