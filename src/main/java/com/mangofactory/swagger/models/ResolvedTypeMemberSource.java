package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public class ResolvedTypeMemberSource implements MemberInfoSource {
    private final ResolvedType resolvedType;

    public ResolvedTypeMemberSource(ResolvedType resolvedType) {
        this.resolvedType = resolvedType;
    }

    @Override
    public Class<?> getType() {
        return resolvedType.getClass();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public ResolvedType getResolvedType() {
        return resolvedType;
    }

    @Override
    public boolean isAssignableFrom(Class<?> clazz) {
        return resolvedType.getClass().isAssignableFrom(clazz) || clazz.isAssignableFrom(resolvedType.getClass());
    }

    @Override
    public boolean isEnum() {
        return EnumHelper.isEnum(resolvedType.getErasedType());
    }
}
