package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedMethod;

public class ResolvedProperty implements MemberInfoSource {
    private final boolean isGetter;
    private String name;
    private ResolvedMethod method;

    public ResolvedProperty(String name, ResolvedMethod method) {
        this.isGetter = true;
        this.name = name;
        this.method = method;
    }

    public ResolvedProperty(String name, ResolvedMethod method, boolean isGetter) {
        this.isGetter = isGetter;
        this.name = name;
        this.method = method;
    }


    public Class<?> getType() {
        return resolvedType().getErasedType();
    }

    private ResolvedType resolvedType() {
        if (isGetter) {
            return method.getReturnType();
        } else {
            return method.getArgumentType(0);
        }
    }

    public String getName() {
        return name;
    }

    public ResolvedType getResolvedType() {
        return resolvedType();
    }

    public String getMethodName() {
        return method.getName();
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
