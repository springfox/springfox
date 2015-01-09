package com.mangofactory.spring.web.readers.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;

@Component
public class ParameterDefaultReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    String defaultValue = findAnnotatedDefaultValue(methodParameter);
    boolean isSkip = ValueConstants.DEFAULT_NONE.equals(defaultValue);
    if (!isSkip) {
      context.parameterBuilder().defaultValue(nullToEmpty(defaultValue));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findAnnotatedDefaultValue(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof RequestParam) {
          return ((RequestParam) annotation).defaultValue();
        } else if (annotation instanceof RequestHeader) {
          return ((RequestHeader) annotation).defaultValue();
        }
      }
    }
    return null;
  }
}
