package com.mangofactory.swagger.core;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.wordnik.swagger.core.util.ModelUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

public final class ModelUtils {

   public static Class<?> getHandlerReturnType(HandlerMethod handlerMethod) {
      Class<?> returnType = handlerMethod.getReturnType().getParameterType();
      ResolvedType resolvedType = new TypeResolver().resolve(returnType);
      return resolvedType.getErasedType();
   }

   public static Class<?> getMethodParameterType(MethodParameter methodParameter) {
      return methodParameter.getParameterType();
   }

   public static String getModelName(Class cls){
      return ModelUtil.toName(cls);
   }

}
