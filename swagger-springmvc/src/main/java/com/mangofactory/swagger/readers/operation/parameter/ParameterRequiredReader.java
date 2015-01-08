package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Component
public class ParameterRequiredReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    context.parameterBuilder().required(getAnnotatedRequired(methodParameter));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private Boolean getAnnotatedRequired(MethodParameter methodParameter) {
    Set<Boolean> requiredSet = new HashSet<Boolean>();
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();

    // when the type is Optional, the required property of @RequestParam/@RequestHeader doesn't matter,
    // since the value is always a non-null Optional after conversion
    boolean optional = isOptional(methodParameter);

    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof RequestParam) {
          requiredSet.add(!optional && ((RequestParam) annotation).required());
        } else if (annotation instanceof RequestHeader) {
          requiredSet.add(!optional && ((RequestHeader) annotation).required());
        } else if (annotation instanceof PathVariable) {
          requiredSet.add(true);
        }
      }
    }
    return requiredSet.contains(true);
  }

  private boolean isOptional(MethodParameter methodParameter) {
    return methodParameter.getParameterType().getName().equals("java.util.Optional");
  }
}
