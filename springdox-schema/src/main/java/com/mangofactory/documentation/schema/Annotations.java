package com.mangofactory.documentation.schema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;

import java.lang.annotation.Annotation;

public class Annotations {

  private Annotations() {
    throw new UnsupportedOperationException();
  }

  /**
   * Finds first annotation of the given type on the given bean property and returns it.
   * Search precedence is getter, setter, field.
   *
   * @param beanPropertyDefinition introspected jackson property definition
   * @param annotationClass        class object representing desired annotation
   * @param <A>                    type that extends Annotation
   * @return first annotation found for property
   */
  public static <A extends Annotation> Optional<A> findPropertyAnnotation(BeanPropertyDefinition beanPropertyDefinition,
                                                                          Class<A> annotationClass) {

    return tryGetGetterAnnotation(beanPropertyDefinition, annotationClass)
            .or(tryGetSetterAnnotation(beanPropertyDefinition, annotationClass))
            .or(tryGetFieldAnnotation(beanPropertyDefinition, annotationClass));
  }

  public static boolean memberIsUnwrapped(AnnotatedMember member) {
    return Optional.fromNullable(member.getAnnotation(JsonUnwrapped.class)).isPresent();
  }

  @SuppressWarnings("PMD")
  private static <A extends Annotation> Optional<A> tryGetGetterAnnotation(
          BeanPropertyDefinition beanPropertyDefinition,
          Class<A> annotationClass) {

    if (beanPropertyDefinition.hasGetter()) {
      return Optional.fromNullable(beanPropertyDefinition.getGetter().getAnnotation(annotationClass));
    }
    return Optional.absent();
  }

  @SuppressWarnings("PMD")
  private static <A extends Annotation> Optional<A> tryGetSetterAnnotation(
          BeanPropertyDefinition beanPropertyDefinition, Class<A> annotationClass) {
    if (beanPropertyDefinition.hasSetter()) {
      return Optional.fromNullable(beanPropertyDefinition.getSetter().getAnnotation(annotationClass));
    }
    return Optional.absent();
  }

  @SuppressWarnings("PMD")
  private static <A extends Annotation> Optional<A> tryGetFieldAnnotation(
          BeanPropertyDefinition beanPropertyDefinition, Class<A> annotationClass) {
    if (beanPropertyDefinition.hasField()) {
      return Optional.fromNullable(beanPropertyDefinition.getField().getAnnotation(annotationClass));
    }
    return Optional.absent();
  }

  public static String memberName(AnnotatedMember member) {
    if (member == null || member.getMember() == null) {
      return "";
    }
    return member.getMember().getName();
  }
}
