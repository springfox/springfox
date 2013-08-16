package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import java.lang.reflect.Type;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

public class WildcardType {
    public static boolean hasWildcards(ResolvedType type) {
        return any(type.getTypeBindings().getTypeParameters(), new Predicate<ResolvedType>() {
            @Override
            public boolean apply(ResolvedType input) {
                return WildcardType.class.equals(input.getErasedType());
            }
        });
    }

    public static boolean exactMatch(ResolvedType first, ResolvedType second) {
        return first.equals(second);
    }

    public static boolean wildcardMatch(ResolvedType toMatch, ResolvedType wildcardType) {
        TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
        TypeBindings bindingsToMatch = toMatch.getTypeBindings();
        if (bindingsToMatch.size() != wildcardTypeBindings.size()) {
            return false;
        }
        for(int index = 0; index < bindingsToMatch.size(); index++) {
            if (!wildcardTypeBindings.getBoundType(index).getErasedType().equals(WildcardType.class)
                    && !wildcardTypeBindings.getBoundType(index).equals(bindingsToMatch.getBoundType(index))) {

                return false;
            }
        }
        return true;
    }

    public static ResolvedType replaceWildcardsFrom(ResolvedType replacingType, ResolvedType wildcardType) {
        TypeBindings wildcardTypeBindings = wildcardType.getTypeBindings();
        TypeBindings bindingsToMatch = replacingType.getTypeBindings();
        Preconditions.checkArgument(bindingsToMatch.size() != wildcardTypeBindings.size());
        List<Type> bindings = newArrayList();
        for(int index = 0; index < bindingsToMatch.size(); index++) {
            if (WildcardType.class.equals(wildcardTypeBindings.getBoundType(index).getErasedType())) {
                bindings.add(bindingsToMatch.getBoundType(index));
            } else {
                bindings.add(wildcardTypeBindings.getBoundType(index));
            }
        }
        return new TypeResolver().resolve(wildcardType.getErasedType(), toArray(bindings, Type.class));
    }
}
