package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public interface MemberInfoSource {
    Class<?> getType();
    String getName();
    ResolvedType getResolvedType();

    boolean isSubclassOf(Class<?> clazz);

    boolean isEnum();
}
