package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Strings;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;

import static com.mangofactory.swagger.models.Annotations.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class BeanModelProperty implements ModelProperty {
    private final BeanPropertyDefinition beanPropertyDefinition;
    private String name;
    private Option<String> propertyDescription;
    private final ResolvedMethod method;
    private final boolean isGetter;
    private TypeResolver typeResolver;
    private final AlternateTypeProvider alternateTypeProvider;

  public BeanModelProperty(BeanPropertyDefinition beanPropertyDefinition, ResolvedMethod method,
            boolean isGetter, TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
        this.beanPropertyDefinition = beanPropertyDefinition;
        this.name = beanPropertyDefinition.getName();
        this.method = method;
        this.isGetter = isGetter;
        this.typeResolver = typeResolver;
        this.alternateTypeProvider = alternateTypeProvider;
    }

    public String getName() {
        return name;
    }

  public void setName(String alias) {
    this.name = alias;
  }


  @Override
    public ResolvedType getType() {
        return alternateTypeProvider.alternateFor(realType());
    }

    private ResolvedType realType() {
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

    @Override
    public String typeName(ModelContext modelContext) {
        return ResolvedTypes.typeName(getType());
    }

    @Override
    public String qualifiedTypeName() {
        if (getType().getTypeParameters().size() > 0) {
            return getType().toString();
        }
        return simpleQualifiedTypeName(getType());
    }

    @Override
    public AllowableValues allowableValues() {
        return ResolvedTypes.allowableValues(getType());
    }

    @Override
    public Option<String> propertyDescription() {
      if (propertyDescription == null) {
        propertyDescription = findPropertyDescription(beanPropertyDefinition);
      }
      return propertyDescription;
    }

    private Option<String> findPropertyDescription(BeanPropertyDefinition beanPropertyDefinition) {
      ApiModelProperty annotation = findPropertyAnnotation(beanPropertyDefinition, ApiModelProperty.class);
      String description = null;
      if (annotation != null) {
        if (!Strings.isNullOrEmpty(annotation.value())) {
          description = annotation.value();
        } else if (!Strings.isNullOrEmpty(annotation.notes())) {
          description = annotation.notes();
        }
      }
      return Option.apply(description);
    }

    @Override
    public boolean isRequired() {
        ApiModelProperty annotation = findPropertyAnnotation(beanPropertyDefinition, ApiModelProperty.class);
        if (annotation != null) {
            return annotation.required();
        }
        return false;
    }

    public static boolean accessorMemberIs(ResolvedMember method, String methodName) {
        return method.getRawMember().getName().equals(methodName);
    }
}
