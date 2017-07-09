/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.builders;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.util.ClassUtils;
import springfox.documentation.RequestHandler;

import java.lang.annotation.Annotation;

public class RequestHandlerSelectors {
  private RequestHandlerSelectors() {
    throw new UnsupportedOperationException();
  }

  /**
   * Any RequestHandler satisfies this condition
   *
   * @return predicate that is always true
   */
  public static Predicate<RequestHandler> any() {
    return Predicates.alwaysTrue();
  }

  /**
   * No RequestHandler satisfies this condition
   *
   * @return predicate that is always false
   */
  public static Predicate<RequestHandler> none() {
    return Predicates.alwaysFalse();
  }

  /**
   * Predicate that matches RequestHandler with handlers methods annotated with given annotation
   *
   * @param annotation - annotation to check
   * @return this
   */
  public static Predicate<RequestHandler> withMethodAnnotation(final Class<? extends Annotation> annotation) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return input.isAnnotatedWith(annotation);
      }
    };
  }

  /**
   * Predicate that matches RequestHandler with given annotation on the declaring class of the handler method
   *
   * @param annotation - annotation to check
   * @return this
   */
  public static Predicate<RequestHandler> withClassAnnotation(final Class<? extends Annotation> annotation) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return declaringClass(input).transform(annotationPresent(annotation)).or(false);
      }
    };
  }

  private static Function<Class<?>, Boolean> annotationPresent(final Class<? extends Annotation> annotation) {
    return new Function<Class<?>, Boolean>() {
      @Override
      public Boolean apply(Class<?> input) {
        return input.isAnnotationPresent(annotation);
      }
    };
  }


  private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
    return new Function<Class<?>, Boolean>() {
      @Override
      public Boolean apply(Class<?> input) {
        return ClassUtils.getPackageName(input).startsWith(basePackage);
      }
    };
  }

  /**
   * Predicate that matches RequestHandler with given base package name for the class of the handler method.
   * This predicate includes all request handlers matching the provided basePackage
   *
   * @param basePackage - base package of the classes
   * @return this
   */
  public static Predicate<RequestHandler> basePackage(final String basePackage) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return declaringClass(input).transform(handlerPackage(basePackage)).or(true);
      }
    };
  }

  private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
    return Optional.fromNullable(input.declaringClass());
  }

}
