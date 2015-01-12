package com.mangofactory.swagger.annotations;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

public class Annotations {

  private Annotations() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ApiParam> findApiParamAnnotation(AnnotatedElement annotated) {
    return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiParam.class));
  }

  public static Optional<ApiResponses> findApiResponsesAnnotations(AnnotatedElement annotated) {
    return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiResponses.class));
  }
}
