package com.mangofactory.swagger.plugins.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;
import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterDefaultReader")
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
        if (annotation instanceof ApiParam && hasText(((ApiParam) annotation).defaultValue())) {
          return ((ApiParam) annotation).defaultValue();
        }
      }
    }
    return null;
  }
}
