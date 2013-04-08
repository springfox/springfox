package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
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
        return resolvedField.getType();
    }

    @Override
    public boolean isAssignableFrom(Class<?> clazz) {
        return getType().isAssignableFrom(clazz) || clazz.isAssignableFrom(getType());
    }

    @Override
    public boolean isEnum() {
        return EnumHelper.isEnum(getType());
    }
}
