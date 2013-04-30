package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.base.Function;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationSchema;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Models.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class ResolvedTypeMemberVisitor implements MemberVisitor {

    private final SchemaProvider context;

    public ResolvedTypeMemberVisitor(SchemaProvider context) {
        this.context = context;
    }

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider input) {
                return new ResolvedTypeMemberVisitor(input);
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public DocumentationSchema schema(MemberInfoSource member) {
        if (context.getSchemaMap().containsKey(member.getType().getSimpleName())) {
            DocumentationSchema schema = new DocumentationSchema();
            schema.setType(modelName(member.getResolvedType()));
            schema.setName(member.getName());
            return schema;
        }
        ResolvedType resolvedMember = member.getResolvedType();
        Class<?> erasedClass = resolvedMember.getErasedType();
        if (resolvedMember.getTypeParameters().size() == 0) {
            if (resolvedMember.isPrimitive() || isPrimitive(resolvedMember.getErasedType())) {
                return PrimitiveMemberVisitor.factory().apply(context).schema(new PrimitiveMemberInfo(erasedClass));
            } else if (EnumHelper.isEnum(resolvedMember.getErasedType())) {
                DocumentationSchema schema = new DocumentationSchema();
                schema.setType(modelName(resolvedMember));
                schema.setName(resolvedMember.getErasedType().getName());
                DocumentationAllowableListValues list = new DocumentationAllowableListValues();
                list.setValues(EnumHelper.getEnumValues(resolvedMember.getErasedType()));
                schema.setAllowableValues(list);
                schema.setProperties(new HashMap<String, DocumentationSchema>());
                context.getSchemaMap().put(schema.getType(), schema);
                return schema;
            } else if (resolvedMember.getErasedType() == Object.class) {
                return null;
            } else if (context.hasCustomSchemaGenerator(resolvedMember)) {
                return context.generateCustomSchema(resolvedMember);
            }
        }
        if (ResolvedCollection.isList(member)) {
            DocumentationSchema schema = new DocumentationSchema();
            schema.setType("List");
            schema.setName(member.getName());
            ResolvedType resolvedType = ResolvedCollection.listElementType(member);
            DocumentationSchema itemSchema = context.schema(resolvedType);
            DocumentationSchema itemSchemaRef = new DocumentationSchema();
            if (itemSchema != null) {
                itemSchemaRef.ref_$eq(itemSchema.getType());
            } else {
                itemSchemaRef.ref_$eq("any");
            }
            schema.setItems(itemSchemaRef);
            return schema;
        } else if (ResolvedCollection.isSet(member)) {
            DocumentationSchema schema = new DocumentationSchema();
            schema.setType("Set");
            schema.setName(member.getName());
            ResolvedType resolvedType = ResolvedCollection.setElementType(member);
            DocumentationSchema itemSchema = context.schema(resolvedType);
            DocumentationSchema itemSchemaRef = new DocumentationSchema();
            if (itemSchema != null) {
                itemSchemaRef.ref_$eq(itemSchema.getType());
            } else {
                itemSchemaRef.ref_$eq("any");
            }
            schema.setItems(itemSchemaRef);
            return schema;
        }

        DocumentationSchema objectSchema = new DocumentationSchema();
        objectSchema.setName(member.getName());
        objectSchema.setType(modelName(resolvedMember));
        context.getSchemaMap().put(resolvedMember.getErasedType().getSimpleName(), objectSchema);
        Map<String, DocumentationSchema> propertyMap = newHashMap();
        for (ResolvedField childField: context.getResolvedFields(resolvedMember)){
            DocumentationSchema childSchema = context.schema(childField);
            if (childSchema != null) {
                propertyMap.put(childField.getName(), childSchema);
            }
        }
        for (ResolvedProperty childProperty: context.getResolvedProperties(resolvedMember)) {
            DocumentationSchema childPropertySchema = context.schema(childProperty);
            if (childPropertySchema != null) {
                propertyMap.put(childProperty.getName(), childPropertySchema);
            }
        }
        objectSchema.setProperties(propertyMap);
        return objectSchema;
    }
}
