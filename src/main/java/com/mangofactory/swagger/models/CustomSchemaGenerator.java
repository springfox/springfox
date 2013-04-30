package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;

public interface CustomSchemaGenerator {
    boolean supports(ResolvedType type);
    Function<SchemaProvider,MemberVisitor> factory();
}
