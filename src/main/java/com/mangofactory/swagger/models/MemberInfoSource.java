package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public interface MemberInfoSource {
    Class<?> getType();
    String getName();
    ResolvedType getResolvedType();

    boolean isAssignableFrom(Class<?> clazz);

    boolean isEnum();
}
