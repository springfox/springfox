package com.mangofactory.spring.web;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.spring.web.readers.operation.HandlerMethodResolver;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Type;

import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.spring.web.readers.operation.HandlerMethodResolver.*;

@Deprecated
public final class ModelUtils {

  private ModelUtils() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType handlerReturnType(TypeResolver resolver, HandlerMethod handlerMethod) {
    Class hostClass = use(handlerMethod.getBeanType())
            .or(handlerMethod.getMethod().getDeclaringClass());
    return new HandlerMethodResolver(resolver).methodReturnType(handlerMethod.getMethod(), hostClass);
  }

  public static String getModelName(TypeResolver resolver, Type clazz) {
    return ResolvedTypes.typeName(asResolved(resolver, clazz));
  }

  public static String getResponseClassName(ResolvedType returnType) {
    return ResolvedTypes.responseTypeName(returnType);
  }
}
