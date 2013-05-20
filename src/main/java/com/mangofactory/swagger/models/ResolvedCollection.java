package com.mangofactory.swagger.models;

import java.util.Collection;
import java.util.Set;

import com.fasterxml.classmate.ResolvedType;

public class ResolvedCollection {

    public static boolean isList(MemberInfoSource member) {
        return Collection.class.isAssignableFrom(member.getType()) && !isSet(member);
    }

    public static ResolvedType listElementType(MemberInfoSource member) {
        ResolvedType resolvedList = member.getResolvedType();
        return resolvedList.typeParametersFor(Collection.class).get(0);
    }

    public static boolean isSet(MemberInfoSource member) {
        return Set.class.isAssignableFrom(member.getType());
    }

    public static ResolvedType setElementType(MemberInfoSource member) {
        ResolvedType resolvedList = member.getResolvedType();
        return resolvedList.typeParametersFor(Set.class).get(0);
    }
}
