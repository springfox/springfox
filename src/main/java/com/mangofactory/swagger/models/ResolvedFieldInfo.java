package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.mangofactory.swagger.SwaggerConfiguration;

public class ResolvedFieldInfo implements MemberInfoSource {
    private final SwaggerConfiguration configuration;
    private final ResolvedField resolvedField;

    public ResolvedFieldInfo(SwaggerConfiguration configuration, ResolvedField resolvedField) {
        this.configuration = configuration;
        this.resolvedField = resolvedField;
    }

    @Override
    public Class<?> getType() {
        return resolvedField.getRawMember().getType();
    }

    @Override
    public String getName() {
        return resolvedField.getRawMember().getName();
    }

    @Override
    public ResolvedType getResolvedType() {
        return configuration.maybeGetAlternateType(internalResolvedType());
    }

    private ResolvedType internalResolvedType() {
        if (resolvedField.getType().getErasedType().getTypeParameters().length > 0) {
            return resolvedField.getType();
        } else {
            return new TypeResolver().resolve(resolvedField.getType().getErasedType());
        }
    }

    @Override
    public boolean isSubclassOf(Class<?> clazz) {
        return (getType() != Object.class) && clazz.isAssignableFrom(getType());
    }

    @Override
    public boolean isEnum() {
        return EnumHelper.isEnum(getType());
    }
}
