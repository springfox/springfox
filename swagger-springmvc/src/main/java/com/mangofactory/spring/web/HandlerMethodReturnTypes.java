package com.mangofactory.spring.web;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.spring.web.readers.operation.HandlerMethodResolver;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.spring.web.readers.operation.HandlerMethodResolver.*;

public final class HandlerMethodReturnTypes {

  private HandlerMethodReturnTypes() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType handlerReturnType(TypeResolver resolver, HandlerMethod handlerMethod) {
    Class hostClass = use(handlerMethod.getBeanType())
            .or(handlerMethod.getMethod().getDeclaringClass());
    return new HandlerMethodResolver(resolver).methodReturnType(handlerMethod.getMethod(), hostClass);
  }
}
