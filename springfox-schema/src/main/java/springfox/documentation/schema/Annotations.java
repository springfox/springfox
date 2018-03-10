/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Function;
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
  public static <A extends Annotation> Optional<A> findPropertyAnnotation(
      BeanPropertyDefinition beanPropertyDefinition,
      Class<A> annotationClass) {

    return tryGetFieldAnnotation(beanPropertyDefinition, annotationClass)
        .or(tryGetGetterAnnotation(beanPropertyDefinition, annotationClass))
        .or(tryGetSetterAnnotation(beanPropertyDefinition, annotationClass));
  }

  public static boolean memberIsUnwrapped(AnnotatedMember member) {
    if (member == null) {
      return false;
    }
    return Optional.fromNullable(member.getAnnotation(JsonUnwrapped.class)).isPresent();
  }

  public static String unwrappedPrefix(AnnotatedMember member) {
    if (member == null) {
      return "";
    }

    return Optional.fromNullable(member.getAnnotation(JsonUnwrapped.class))
        .transform(new Function<JsonUnwrapped,
            String>() {
          @Override
          public String apply(JsonUnwrapped input) {
            return input.prefix();
          }
        }).or("");
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
      BeanPropertyDefinition beanPropertyDefinition,
      Class<A> annotationClass) {

    if (beanPropertyDefinition.hasSetter()) {
      return Optional.fromNullable(beanPropertyDefinition.getSetter().getAnnotation(annotationClass));
    }
    return Optional.absent();
  }

  @SuppressWarnings("PMD")
  private static <A extends Annotation> Optional<A> tryGetFieldAnnotation(
      BeanPropertyDefinition beanPropertyDefinition,
      Class<A> annotationClass) {
    
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
