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

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Optional.*;

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
    return annotationFromBean(context, annotationType)
       .map(Optional::of).orElse(annotationFromField(context, annotationType));
  }

  public static <T extends Annotation> Optional<T> annotationFromBean(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<BeanPropertyDefinition> propertyDefinition = context.getBeanPropertyDefinition();
    if (propertyDefinition.isPresent()) {
      Optional<T> fromGetter = extractGetterFromPropertyDefinition(propertyDefinition.get())
          .map(m -> AnnotationUtils.findAnnotation(m, annotationType));
      if (fromGetter.isPresent()) {
        return fromGetter;
      }
      return extractFieldFromPropertyDefinition(propertyDefinition.get())
          .map(f -> AnnotationUtils.findAnnotation(f, annotationType));
    }
    return Optional.empty();
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

  private static Optional<Field> extractFieldFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getField() != null) {
      return ofNullable(propertyDefinition.getField().getAnnotated());
    }
    return empty();
  }

  private static Optional<Method> extractGetterFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getGetter() != null) {
      return ofNullable(propertyDefinition.getGetter().getMember());
    }
    return empty();
  }

  private static <T extends Annotation> Optional<T> findAnnotation(
      Optional<? extends AnnotatedElement> annotatedElement,
      Class<T> annotationType) {
    return annotatedElement
        .map(annotated -> AnnotationUtils.findAnnotation(annotated, annotationType));
  }
}
