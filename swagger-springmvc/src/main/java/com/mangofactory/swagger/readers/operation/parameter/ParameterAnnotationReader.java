package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

class ParameterAnnotationReader {
  private static <A extends Annotation> Predicate<? super Annotation> annotationOfType(final Class<A> annotationType) {
    return new Predicate<Annotation>() {
      @Override
      public boolean apply(Annotation input) {
        return input.annotationType().equals(annotationType);
      }
    };
  }

  private static Optional<Method> interfaceMethod(Class<?> iface, Method method) {
    try {
      return Optional.of(iface.getMethod(method.getName(), method.getParameterTypes()));
    } catch (NoSuchMethodException ex) {
      return Optional.absent();
    }
  }

  @SuppressWarnings("unchecked")
  static <A extends Annotation> A searchOnInterfaces(Method method,
                                              int parameterIndex,
                                              Class<A> annotationType,
                                              Class<?>[] interfaces) {

    A annotation = null;
    for (Class<?> interfaze : interfaces) {
      Optional<Method> interfaceMethod = interfaceMethod(interfaze, method);
      if (interfaceMethod.isPresent()) {
        Method superMethod = interfaceMethod.get();
        Optional<Annotation> found = tryFind(
                newArrayList(superMethod.getParameterAnnotations()[parameterIndex]), annotationOfType(annotationType));
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

  <A extends Annotation> Optional<A> fromHierarchy(MethodParameter methodParameter, Class<A> annotationType) {
    return Optional.fromNullable(searchOnInterfaces(methodParameter.getMethod(),
            methodParameter.getParameterIndex(),
            annotationType,
            getParentInterfaces(methodParameter)));
  }

  Class<?>[] getParentInterfaces(MethodParameter methodParameter) {
    return methodParameter.getMethod().getDeclaringClass().getInterfaces();
  }
}
