package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.wordnik.swagger.core.DocumentationSchema;

public class ObjectMemberVisitor implements MemberVisitor {
    private static MemberVisitor instance = new ObjectMemberVisitor();

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider context) {
                return instance;
            }
        };
    }

    @Override
    public DocumentationSchema schema(MemberInfoSource member) {
        DocumentationSchema propertySchema = new DocumentationSchema();
        propertySchema.setName(member.getName());
        return propertySchema;
    }
}
