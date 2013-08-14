package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public  interface TypeProcessingRule {
    boolean isIgnorable();
    boolean hasAlternateType();
    ResolvedType originalType();
    ResolvedType alternateType(ResolvedType parameterType);
}

