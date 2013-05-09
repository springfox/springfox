package com.mangofactory.swagger.models;

import com.google.common.base.Objects;

public final class AlternateTypeProcessingRule implements TypeProcessingRule {
    private Class<?> alternateType;
    private Class<?> originalType;

    public AlternateTypeProcessingRule(Class<?> originalType, Class<?> alternateType) {
        this.alternateType = alternateType;
        this.originalType = originalType;
    }

    @Override
    public boolean isIgnorable() {
        return false;
    }

    @Override
    public boolean hasAlternateType() {
        return !Objects.equal(originalType, alternateType);
    }

    @Override
    public Class<?> originalType() {
        return originalType;
    }

    @Override
    public Class<?> alternateType() {
        return alternateType;
    }
}
