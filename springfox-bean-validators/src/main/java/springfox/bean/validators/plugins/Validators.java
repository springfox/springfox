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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

import javax.validation.groups.Default;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

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

  public static boolean annotationMustBeApplied(ModelPropertyContext context, Class<?>[] groupsConstraint) {
    // Case #1: @Validated and @Valid is not presented
    // set of resolved type must be empty. This case breaks tests. Return must be true.
    Set<ResolvedType> validatedGroups = context.getOwner().getValidationGroups();
    if (validatedGroups.isEmpty()) {
      return false;
    }

    Set<Class<?>> validatedGroupsClasses = getGroupClasses(validatedGroups.stream().map(ResolvedType::getErasedType));
    Set<Class<?>> constraintGroupClasses = getGroupClasses(Stream.of(groupsConstraint));

    // Case #2: Validated groups contains Default.class
    // Constrain groups contains Default.class
    if (validatedGroupsClasses.contains(Default.class)) {
      return constraintGroupClasses.isEmpty() || constraintGroupClasses.contains(Default.class);
    }

    // Case #3: Validated groups does not contain Default.class
    // Constrain groups may contain Default.class
    return !Collections.disjoint(validatedGroupsClasses, constraintGroupClasses);
  }

  private static Set<Class<?>> getGroupClasses(Stream<Class<?>> stream) {
    return stream.flatMap(i -> getSuperClasses(i).stream()).collect(Collectors.toSet());
  }

  public static Set<Class<?>> getSuperClasses(Class<?> c) {
    Set<Class<?>> classSet = new HashSet<>();
    classSet.add(c);
    for (Class<?> superclass = c; superclass != null; c = superclass) {
      classSet.add(superclass);
      superclass = c.getSuperclass();
    }
    return classSet;
  }
}
