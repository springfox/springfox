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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;

import springfox.bean.apidescriptionreaders.plugins.ParameterDescriptionKeysAnnotationPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

/**
 * Utility methods for BeanValidators
 *
 */
public class BeanValidators {
  public final static int BEAN_VALIDATOR_PLUGIN_ORDER = Ordered.HIGHEST_PRECEDENCE + 500;

  private static final Logger LOG = LoggerFactory.getLogger(BeanValidators.class);
  
  private BeanValidators() {
    throw new UnsupportedOperationException();
  }

  /**
   * read annotation from ModelProperty bean (getter/setter)
   * @param context
   * @param annotationType
   * @return
   */
  public static <T extends Annotation> Optional<T> validatorFromModelPropertyBean(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<BeanPropertyDefinition> propertyDefinition = context.getBeanPropertyDefinition();
    Optional<T> myAnnotation = Optional.absent();
    if (propertyDefinition.isPresent()) {
      myAnnotation = annotationFrom(propertyDefinition.get().getGetter(), annotationType)
                .or(annotationFrom(propertyDefinition.get().getField(), annotationType));
    }
    return myAnnotation;
  }

  /**
   * read annotation from ModelProperty field
   * @param context
   * @param annotationType
   * @return
   */
  public static <T extends Annotation> Optional<T> validatorFromModelPropertyField(
      ModelPropertyContext context,
      Class<T> annotationType) {

    Optional<AnnotatedElement> annotatedElement = context.getAnnotatedElement();
    Optional<T> myAnnotation = Optional.absent();
    if (annotatedElement.isPresent()) {
      myAnnotation = Optional.fromNullable(annotatedElement.get().getAnnotation(annotationType));
    }
    return myAnnotation;
  }

  /**
   * read annotation from ParameterExpansion field
   * @param context
   * @param annotationType
   * @return
   */
  public static <T extends Annotation> Optional<T> validatorFromParameterExpansionField(ParameterExpansionContext context, Class<T> annotationType) {

      Field field = context.getField();
      Optional<T> myAnnotation = Optional.absent();
      if (field != null) {
          LOG.debug("Annotation size present for field " + field.getName() + "!!");
          myAnnotation = Optional.fromNullable(field.getAnnotation(annotationType));
      }

      return myAnnotation;
  }
  

  /**
   * read annotation from Parameter field
   * @param context
   * @param annotationType
   * @return
   */
  public static <T extends Annotation> Optional<T> validatorFromParameterField(ParameterContext context, Class<T> annotationType) {

      MethodParameter methodParam = context.methodParameter();

      T annotatedElement = methodParam.getParameterAnnotation(annotationType);
      Optional<T> myAnnotation = Optional.absent();
      if (annotatedElement != null) {
          myAnnotation = Optional.fromNullable(annotatedElement);
      }
      return myAnnotation;
  }
  
  /**
   * read annotation from getter/field (AnnotatedMember)
   * @param nullableMember
   * @param annotationType
   * @return
   */
  @VisibleForTesting
  static <T extends Annotation> Optional<T> annotationFrom(
      AnnotatedMember nullableMember,
      Class<T> annotationType) {

    Optional<AnnotatedMember> member = Optional.fromNullable(nullableMember);
    Optional<T> myAnnotation = Optional.absent();
    if (member.isPresent()) {
      myAnnotation = FluentIterable.from(member.get().annotations())
          .filter(annotationType)
          .first();
    }
    return myAnnotation;
  }

}
