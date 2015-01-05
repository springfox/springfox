package com.mangofactory.schema.property.bean;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.BaseModelProperty;


public class BeanModelProperty extends BaseModelProperty {

  private final ResolvedMethod method;
  private final boolean isGetter;
  private TypeResolver typeResolver;


  public BeanModelProperty(String propertyName, ResolvedMethod method,
                           boolean isGetter, TypeResolver typeResolver,
                           AlternateTypeProvider alternateTypeProvider) {

    super(propertyName, alternateTypeProvider);

    this.method = method;
    this.isGetter = isGetter;
    this.typeResolver = typeResolver;
  }

  public static boolean accessorMemberIs(ResolvedMember method, String methodName) {
    return method.getRawMember().getName().equals(methodName);
  }

  @Override
  protected ResolvedType realType() {
    if (isGetter) {
      if (method.getReturnType().getErasedType().getTypeParameters().length > 0) {
        return method.getReturnType();
      } else {
        return typeResolver.resolve(method.getReturnType().getErasedType());
      }
    } else {
      if (method.getArgumentType(0).getErasedType().getTypeParameters().length > 0) {
        return method.getArgumentType(0);
      } else {
        return typeResolver.resolve(method.getArgumentType(0).getErasedType());
      }
    }
  }
}
