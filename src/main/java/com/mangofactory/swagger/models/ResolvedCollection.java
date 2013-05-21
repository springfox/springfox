package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

import java.util.List;
import java.util.Set;

public class ResolvedCollection {

    public static boolean isList(MemberInfoSource member) {
        return member.isSubclassOf(List.class);
    }

    public static ResolvedType listElementType(MemberInfoSource member) {
        ResolvedType resolvedList = member.getResolvedType();
        return resolvedList.typeParametersFor(List.class).get(0);
    }

    public static boolean isSet(MemberInfoSource member) {
        return member.isSubclassOf(Set.class);
    }

    public static ResolvedType setElementType(MemberInfoSource member) {
        ResolvedType resolvedList = member.getResolvedType();
        return resolvedList.typeParametersFor(Set.class).get(0);
    }
}
