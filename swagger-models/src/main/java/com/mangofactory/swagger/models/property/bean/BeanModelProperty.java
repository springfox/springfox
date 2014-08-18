package com.mangofactory.swagger.models.property.bean;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.BaseModelProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

import static com.mangofactory.swagger.models.Annotations.*;

public class BeanModelProperty extends BaseModelProperty {

  private final ResolvedMethod method;
  private final boolean isGetter;
  private TypeResolver typeResolver;


  public BeanModelProperty(String propertyName, BeanPropertyDefinition beanPropertyDefinition, ResolvedMethod method,
                           boolean isGetter, TypeResolver typeResolver,
                           AlternateTypeProvider alternateTypeProvider) {

    super(propertyName, alternateTypeProvider,
            Optional.fromNullable(findPropertyAnnotation(beanPropertyDefinition, ApiModelProperty.class)));

    this.method = method;
    this.isGetter = isGetter;
    this.typeResolver = typeResolver;
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

  public static boolean accessorMemberIs(ResolvedMember method, String methodName) {
    return method.getRawMember().getName().equals(methodName);
  }
}
