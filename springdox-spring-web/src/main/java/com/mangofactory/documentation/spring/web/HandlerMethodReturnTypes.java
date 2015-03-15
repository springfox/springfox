package com.mangofactory.documentation.spring.web;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.documentation.spring.web.readers.operation.HandlerMethodResolver;
import org.springframework.web.method.HandlerMethod;

public final class HandlerMethodReturnTypes {

  private HandlerMethodReturnTypes() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType handlerReturnType(TypeResolver resolver, HandlerMethod handlerMethod) {
    Class hostClass = useType(handlerMethod.getBeanType())
            .or(handlerMethod.getMethod().getDeclaringClass());
    return new HandlerMethodResolver(resolver).methodReturnType(handlerMethod.getMethod(), hostClass);
  }

  public static Optional<Class> useType(Class beanType) {
    if (Class.class.getName().equals(beanType.getName())) {
      return Optional.absent();
    }
    return Optional.fromNullable(beanType);
  }
}
