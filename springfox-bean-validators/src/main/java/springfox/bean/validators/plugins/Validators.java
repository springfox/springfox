/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
package springfox.bean.validators.plugins;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.util.Predicates;

/**
 * Utility methods for Validators
 */
public class Validators {
  public static final int BEAN_VALIDATOR_PLUGIN_ORDER = Ordered.HIGHEST_PRECEDENCE + 500;

  private Validators() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Annotation> Optional<T> extractAnnotation(
      ModelPropertyContext context,
      Class<T> annotationType) {
    return Predicates.or(annotationFromBean(context, annotationType),
                         annotationFromField(context, annotationType));
  }

  public static <T extends Annotation> Optional<T> annotationFromBean(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<BeanPropertyDefinition> propertyDefinition = context.getBeanPropertyDefinition();
    Optional<T> notNull = Optional.empty();
    if (propertyDefinition.isPresent()) {
      Optional<Method> getter = extractGetterFromPropertyDefinition(propertyDefinition.get());
      Optional<Field> field = extractFieldFromPropertyDefinition(propertyDefinition.get());
      notNull = Predicates.or(findAnnotation(getter, annotationType), findAnnotation(field, annotationType));
    }

    return notNull;
  }

  public static <T extends Annotation> Optional<T> annotationFromField(
      ModelPropertyContext context,
      Class<T> annotationType) {
    return findAnnotation(context.getAnnotatedElement(), annotationType);
  }

  public static <T extends Annotation> Optional<T> annotationFromParameter(
      ParameterContext context,
      Class<T> annotationType) {

    ResolvedMethodParameter methodParam = context.resolvedMethodParameter();
    return methodParam.findAnnotation(annotationType);
  }

  public static <T extends Annotation> Optional<T> validatorFromExpandedParameter(
      ParameterExpansionContext context,
      Class<T> annotationType) {

    Field field = context.getField().getRawMember();
    return Optional.ofNullable(field.getAnnotation(annotationType));
  }

  private static Optional<Field> extractFieldFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getField() != null) {
      return Optional.ofNullable(propertyDefinition.getField().getAnnotated());
    }
    return Optional.empty();
  }

  private static Optional<Method> extractGetterFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getGetter() != null) {
      return Optional.ofNullable(propertyDefinition.getGetter().getMember());
    }
    return Optional.empty();
  }

  private static <T extends Annotation> Optional<T> findAnnotation(
      Optional<? extends AnnotatedElement> annotatedElement,
      Class<T> annotationType) {
    if (annotatedElement.isPresent()) {
      return Optional.ofNullable(AnnotationUtils.findAnnotation(annotatedElement.get(), annotationType));
    } else {
      return Optional.empty();
    }
  }
}
