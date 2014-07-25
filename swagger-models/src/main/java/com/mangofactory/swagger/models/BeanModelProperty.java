package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.model.AllowableValues;
import org.springframework.core.annotation.AnnotationUtils;
import scala.Option;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class BeanModelProperty implements ModelProperty {
    private String name;
    private final ResolvedMethod method;
    private final boolean isGetter;
    private TypeResolver typeResolver;
    private final AlternateTypeProvider alternateTypeProvider;
    private final String propertyDescription;

  public BeanModelProperty(String name, String propertyDescription, ResolvedMethod method,
            boolean isGetter, TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
        this.name = name;
        this.propertyDescription = propertyDescription;
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
        return Option.apply(propertyDescription);
    }


    @Override
    public boolean isRequired() {
        ApiModelProperty annotation = AnnotationUtils.findAnnotation(method.getRawMember(), ApiModelProperty.class);
        if (annotation != null) {
            return annotation.required();
        }
        return false;
    }

    public static boolean accessorMemberIs(ResolvedMember method, String methodName) {
        return method.getRawMember().getName().equals(methodName);
    }
}
