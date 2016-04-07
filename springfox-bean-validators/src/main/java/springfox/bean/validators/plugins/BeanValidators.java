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

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.springframework.core.Ordered;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class BeanValidators {
  public final static int BEAN_VALIDATOR_PLUGIN_ORDER = Ordered.HIGHEST_PRECEDENCE + 500;

  private BeanValidators() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Annotation> Optional<T> validatorFromBean(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<BeanPropertyDefinition> propertyDefinition = context.getBeanPropertyDefinition();
    Optional<T> notNull = Optional.absent();
    if (propertyDefinition.isPresent()) {
      notNull = annotationFrom(propertyDefinition.get().getGetter(), annotationType)
                .or(annotationFrom(propertyDefinition.get().getField(), annotationType));
    }
    return notNull;
  }

  public static <T extends Annotation> Optional<T> validatorFromField(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<AnnotatedElement> annotatedElement = context.getAnnotatedElement();
    Optional<T> notNull = Optional.absent();
    if (annotatedElement.isPresent()) {
      notNull = Optional.fromNullable(annotatedElement.get().getAnnotation(annotationType));
    }
    return notNull;
  }

  @VisibleForTesting
  static <T extends Annotation> Optional<T> annotationFrom(
      AnnotatedMember nullableMember,
      Class<T> annotationType) {

    Optional<AnnotatedMember> member = Optional.fromNullable(nullableMember);
    Optional<T> notNull = Optional.absent();
    if (member.isPresent()) {
      notNull = FluentIterable.from(member.get().annotations())
          .filter(annotationType)
          .first();
    }
    return notNull;
  }

}
