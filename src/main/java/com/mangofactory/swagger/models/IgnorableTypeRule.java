package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

import static com.mangofactory.swagger.models.ResolvedTypes.asResolvedType;

public class IgnorableTypeRule implements TypeProcessingRule{
    private ResolvedType ignorableType;

    public IgnorableTypeRule(Class<?> ignorableType) {
        this.ignorableType = asResolvedType(ignorableType);
    }

    @Override
    public boolean isIgnorable() {
        return true;
    }

    @Override
    public boolean hasAlternateType() {
        return false;
    }

    @Override
    public ResolvedType originalType() {
        return ignorableType;
    }

    @Override
    public ResolvedType alternateType(ResolvedType parameterType) {
        return parameterType;
    }

    public static IgnorableTypeRule ignorable(Class<?> clazz) {
        return new IgnorableTypeRule(clazz);
    }
}
