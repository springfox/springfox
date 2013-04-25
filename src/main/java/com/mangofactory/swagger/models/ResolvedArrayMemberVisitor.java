package com.mangofactory.swagger.models;

import com.fasterxml.classmate.types.ResolvedArrayType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.wordnik.swagger.core.DocumentationSchema;

import static com.mangofactory.swagger.models.ResolvedTypes.modelName;

public class ResolvedArrayMemberVisitor implements MemberVisitor {
    private final SchemaProvider context;

    public ResolvedArrayMemberVisitor(SchemaProvider context) {
        this.context = context;
    }

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider input) {
                return new ResolvedArrayMemberVisitor(input);
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public DocumentationSchema schema(MemberInfoSource member) {
        Preconditions.checkArgument(member.getResolvedType() instanceof ResolvedArrayType);
        if (context.getSchemaMap().containsKey(member.getType().getSimpleName())) {
            DocumentationSchema schema = new DocumentationSchema();
            schema.setType(modelName(member.getResolvedType()));
            schema.setName(member.getName());
            return schema;
        }
        ResolvedArrayType resolvedArrayType = (ResolvedArrayType) member.getResolvedType();
        DocumentationSchema schema = new DocumentationSchema();
        schema.setType("Array");
        schema.setName(member.getName());
        DocumentationSchema itemSchema = context.schema(resolvedArrayType.getArrayElementType());
        DocumentationSchema itemSchemaRef = new DocumentationSchema();
        itemSchemaRef.ref_$eq(itemSchema.getType());
        schema.setItems(itemSchemaRef);
        return schema;

    }
}
