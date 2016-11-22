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
package springfox.bean.validators.plugins;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BeanValidators {
  public final static int BEAN_VALIDATOR_PLUGIN_ORDER = Ordered.HIGHEST_PRECEDENCE + 500;

  private BeanValidators() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Annotation> Optional<T> extractAnnotation(
          ModelPropertyContext context,
          Class<T> annotationType) {
    return validatorFromBean(context, annotationType)
            .or(validatorFromField(context, annotationType));
  }

  public static <T extends Annotation> Optional<T> validatorFromBean(
          ModelPropertyContext context,
          Class<T> annotationType) {

    Optional<BeanPropertyDefinition> propertyDefinition = context.getBeanPropertyDefinition();
    Optional<T> notNull = Optional.absent();
    if (propertyDefinition.isPresent()) {
      Optional<Method> getter = extractGetterFromPropertyDefinition(propertyDefinition.get());
      Optional<Field> field = extractFieldFromPropertyDefinition(propertyDefinition.get());
      notNull = findAnnotation(getter, annotationType).or(findAnnotation(field, annotationType));
    }

    return notNull;
  }

  public static <T extends Annotation> Optional<T> validatorFromField(
          ModelPropertyContext context,
          Class<T> annotationType) {
    return findAnnotation(context.getAnnotatedElement(), annotationType);
  }

  private static Optional<Field> extractFieldFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getField() != null) {
      return Optional.fromNullable(propertyDefinition.getField().getAnnotated());
    }
    return Optional.absent();
  }

  private static Optional<Method> extractGetterFromPropertyDefinition(BeanPropertyDefinition propertyDefinition) {
    if (propertyDefinition.getGetter() != null) {
      return Optional.fromNullable(propertyDefinition.getGetter().getMember());
    }
    return Optional.absent();
  }

  private static <T extends Annotation> Optional<T> findAnnotation(
          Optional<? extends AnnotatedElement> annotatedElement,
          Class<T> annotationType) {
    if (annotatedElement.isPresent()) {
      return Optional.fromNullable(AnnotationUtils.findAnnotation(annotatedElement.get(), annotationType));
    } else {
      return Optional.absent();
    }
  }
}
