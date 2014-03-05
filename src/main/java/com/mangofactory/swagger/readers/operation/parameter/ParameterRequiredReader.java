package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class ParameterRequiredReader implements Command<RequestMappingContext> {
  @Override
  public void execute(RequestMappingContext context) {
    MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
    context.put("required", getAnnotatedRequired(methodParameter));
  }

  private Boolean getAnnotatedRequired(MethodParameter methodParameter) {
    Set<Boolean> requiredSet = new HashSet<Boolean>();
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof ApiParam) {
          requiredSet.add(((ApiParam) annotation).required());
        } else if (annotation instanceof RequestParam) {
          requiredSet.add(((RequestParam) annotation).required());
        } else if (annotation instanceof RequestHeader) {
          requiredSet.add(((RequestHeader) annotation).required());
        } else if (annotation instanceof PathVariable) {
          requiredSet.add(true);
        }
      }
    }
    return requiredSet.contains(true);
  }
}
