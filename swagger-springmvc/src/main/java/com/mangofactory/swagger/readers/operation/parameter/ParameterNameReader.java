package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;
import static java.lang.String.*;
import static org.springframework.util.StringUtils.*;

public class ParameterNameReader implements Command<RequestMappingContext> {

  private ParameterAnnotationReader annotations = new ParameterAnnotationReader();

  public ParameterNameReader() {
  }

  @VisibleForTesting
  ParameterNameReader(ParameterAnnotationReader annotations) {
    this.annotations = annotations;
  }

  @Override
  public void execute(RequestMappingContext context) {
    MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
//    Optional<ApiParam> apiParam = Optional.fromNullable(methodParameter.getParameterAnnotation(ApiParam.class))
//            .or(annotations.fromHierarchy(methodParameter, ApiParam.class));

    Optional<ApiParam> apiParam = Optional.fromNullable(methodParameter.getParameterAnnotation(ApiParam.class));
    apiParam = apiParam.or(annotations.fromHierarchy(methodParameter, ApiParam.class));
    String name;
    if (apiParam.isPresent() && hasText(apiParam.get().name())) {
      name = apiParam.get().name();
    } else {
      name = findParameterNameFromAnnotations(methodParameter);
      if (isNullOrEmpty(name)) {
        String parameterName = methodParameter.getParameterName();
        name = isNullOrEmpty(parameterName) ? format("param%s", methodParameter.getParameterIndex()) : parameterName;
      }
    }
    context.put("name", name);
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
