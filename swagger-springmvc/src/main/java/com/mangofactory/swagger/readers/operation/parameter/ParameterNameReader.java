package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;
import static java.lang.String.*;

@Component
public class ParameterNameReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    String name = findParameterNameFromAnnotations(methodParameter);
    String parameterName = methodParameter.getParameterName();
    if (isNullOrEmpty(name)) {
      name = isNullOrEmpty(parameterName) ? format("param%s", methodParameter.getParameterIndex()) : parameterName;
    }
    context.parameterBuilder().name(name);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findParameterNameFromAnnotations(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof PathVariable) {
          return ((PathVariable) annotation).value();
        } else if (annotation instanceof ModelAttribute) {
          return ((ModelAttribute) annotation).value();
        } else if (annotation instanceof RequestParam) {
          return ((RequestParam) annotation).value();
        } else if (annotation instanceof RequestHeader) {
          return ((RequestHeader) annotation).value();
        }
      }
    }
    return null;
  }
}
