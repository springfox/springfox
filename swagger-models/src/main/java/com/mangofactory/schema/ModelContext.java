package com.mangofactory.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.ResolvedTypes.*;

public class ModelContext {
  private final ModelContext parentContext;
  private final Type type;
  private final boolean returnType;
  private Set<ResolvedType> seenTypes = newHashSet();

  ModelContext(Type type, boolean returnType) {
    this.parentContext = null;
    this.type = type;
    this.returnType = returnType;
  }

  ModelContext(ModelContext parentContext, ResolvedType input) {
    this.parentContext = parentContext;
    this.type = input;
    this.returnType = parentContext.isReturnType();
  }

  public Type getType() {
    return type;
  }

  public ResolvedType resolvedType(TypeResolver resolver) {
    return asResolved(resolver, getType());
  }

  public boolean isReturnType() {
    return returnType;
  }

  public static ModelContext inputParam(Type type) {
    return new ModelContext(type, false);
  }

  public static ModelContext returnValue(Type type) {
    return new ModelContext(type, true);
  }

  public static ModelContext fromParent(ModelContext context, ResolvedType input) {
    return new ModelContext(context, input);
  }

  public boolean hasSeenBefore(ResolvedType resolvedType) {
    return seenTypes.contains(resolvedType)
            || seenTypes.contains(asResolved(new TypeResolver(), resolvedType.getErasedType()))
            || parentHasSeenBefore(resolvedType);
  }

  private boolean parentHasSeenBefore(ResolvedType resolvedType) {
    if (parentContext == null) {
      return false;
    }
    return parentContext.hasSeenBefore(resolvedType);
  }

  public void seen(ResolvedType resolvedType) {
    seenTypes.add(resolvedType);
  }
}
