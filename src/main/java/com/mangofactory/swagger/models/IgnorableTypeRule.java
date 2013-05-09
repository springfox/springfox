package com.mangofactory.swagger.models;

public class IgnorableTypeRule implements TypeProcessingRule{
    private Class<?> ignorableType;

    public IgnorableTypeRule(Class<?> ignorableType) {
        this.ignorableType = ignorableType;
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
    public Class<?> originalType() {
        return ignorableType;
    }

    @Override
    public Class<?> alternateType() {
        return ignorableType;
    }

    public static IgnorableTypeRule ignorable(Class<?> clazz) {
        return new IgnorableTypeRule(clazz);
    }
}
