package com.mangofactory.swagger.models;

public  interface TypeProcessingRule {
    boolean isIgnorable();
    boolean hasAlternateType();
    Class<?> originalType();
    Class<?> alternateType();
}
