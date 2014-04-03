package com.mangofactory.swagger.models;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;

import static com.google.common.collect.Lists.newArrayList;

public class FieldsProvider {
    private final TypeResolver typeResolver;

    public FieldsProvider(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public Iterable<? extends ResolvedField> in(ResolvedType resolvedType) {
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        if (resolvedType.getErasedType() == Object.class) {
            return newArrayList();
        }
        ResolvedTypeWithMembers resolvedMemberWithMembers = memberResolver.resolve(resolvedType, null, null);
        return newArrayList(resolvedMemberWithMembers.getMemberFields());
    }
}
