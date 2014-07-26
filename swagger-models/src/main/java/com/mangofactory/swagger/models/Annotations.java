package com.mangofactory.swagger.models;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.lang.annotation.Annotation;

public class Annotations {
  /**
   * Finds first annotation of the given type on the given bean property and returns it.
   * Search precedence is getter -> setter -> field.
   *
   * @param beanPropertyDefinition introspected jackson proprty defintion
   * @param annotationClass class object representing desired annotation
   * @param <A> type that extends Annotation
   * @return first annotation found for property
   */
  public static <A extends Annotation> A findPropertyAnnotation(BeanPropertyDefinition beanPropertyDefinition,
                                                  Class<A> annotationClass) {
    A annotation = null;
    if (beanPropertyDefinition.hasGetter()) {
      annotation = beanPropertyDefinition.getGetter().getAnnotation(annotationClass);
    }
    if (annotation == null && beanPropertyDefinition.hasSetter()) {
      annotation = beanPropertyDefinition.getSetter().getAnnotation(annotationClass);
    }
    if (annotation == null && beanPropertyDefinition.hasField()) {
        annotation = beanPropertyDefinition.getField().getAnnotation(annotationClass);
    }

    return annotation;
  }
}
