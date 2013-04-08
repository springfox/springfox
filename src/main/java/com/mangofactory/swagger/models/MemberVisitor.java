package com.mangofactory.swagger.models;

import com.wordnik.swagger.core.DocumentationSchema;

public interface MemberVisitor {
    DocumentationSchema schema(MemberInfoSource property);
}
