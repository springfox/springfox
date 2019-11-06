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

package springfox.documentation.swagger.readers.parameter;

import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.*;

public class ParameterAnnotationReader {
  private ParameterAnnotationReader() {
    throw new UnsupportedOperationException();
  }

  public static <A extends Annotation> Optional<A> fromHierarchy(
      MethodParameter methodParameter,
      Class<A> annotationType) {
    return ofNullable(searchOnInterfaces(methodParameter.getMethod(),
        methodParameter.getParameterIndex(),
        annotationType,
        getParentInterfaces(methodParameter)));
  }

  private static Optional<Method> interfaceMethod(Class<?> iface, Method method) {
    try {
      return of(iface.getMethod(method.getName(), method.getParameterTypes()));
    } catch (NoSuchMethodException ex) {
      return empty();
    }
  }

  @SuppressWarnings("unchecked")
  private static <A extends Annotation> A searchOnInterfaces(
      Method method,
      int parameterIndex,
      Class<A> annotationType,
      Class<?>[] interfaces) {

    A annotation = null;
    for (Class<?> interfaze : interfaces) {
      Optional<Method> interfaceMethod = interfaceMethod(interfaze, method);
      if (interfaceMethod.isPresent()) {
        Method superMethod = interfaceMethod.get();
        Optional<Annotation> found = Stream.of(
                superMethod.getParameterAnnotations()[parameterIndex])
            .filter(input -> input.annotationType().equals(annotationType)).findFirst();
        if (found.isPresent()) {
          annotation = (A) found.get();
          break;
        }
        Class<?>[] superInterfaces = superMethod.getDeclaringClass().getInterfaces();
        annotation = searchOnInterfaces(superMethod, parameterIndex, annotationType, superInterfaces);
      }
    }
    return annotation;
  }

  private static Class<?>[] getParentInterfaces(MethodParameter methodParameter) {
    return methodParameter.getMethod().getDeclaringClass().getInterfaces();
  }
}
