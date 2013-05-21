package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;

public class ResolvedFieldInfo implements MemberInfoSource {
    private final ResolvedField resolvedField;

    public ResolvedFieldInfo(ResolvedField resolvedField) {
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
