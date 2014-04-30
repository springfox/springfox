package com.mangofactory.swagger.core;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Type;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public final class ModelUtils {

   public static ResolvedType handlerReturnType(TypeResolver resolver, HandlerMethod handlerMethod) {
       return new HandlerMethodResolver(resolver).methodReturnType(handlerMethod.getMethod());
   }

   public static String getModelName(TypeResolver resolver, Type clazz){
       return ResolvedTypes.typeName(asResolved(resolver, clazz));
   }

    public static String getResponseClassName(ResolvedType returnType) {
        return ResolvedTypes.responseTypeName(returnType);
    }
}
