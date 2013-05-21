package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

public class PrimitiveMemberInfo implements MemberInfoSource {
    private final Class<?> clazz;

    public PrimitiveMemberInfo(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public ResolvedType getResolvedType() {
        TypeResolver resolver = new TypeResolver();
        return resolver.resolve(clazz);
    }

    @Override
    public boolean isSubclassOf(Class<?> clazz) {
        return clazz.isAssignableFrom(getType());
    }

    @Override
    public boolean isEnum() {
        return EnumHelper.isEnum(getType());
    }
}
