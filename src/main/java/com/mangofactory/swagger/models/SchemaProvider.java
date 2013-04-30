package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedInterfaceType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.fasterxml.classmate.types.ResolvedRecursiveType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.core.DocumentationSchema;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Maps.*;

public class SchemaProvider {

    private static final Map<Class, Function<SchemaProvider,MemberVisitor>> propertySchemas = ImmutableMap
            .<Class, Function<SchemaProvider, MemberVisitor>>builder()
            .put(double.class, PrimitiveMemberVisitor.factory())
            .put(Double.class, PrimitiveMemberVisitor.factory())
            .put(int.class, PrimitiveMemberVisitor.factory())
            .put(Integer.class, PrimitiveMemberVisitor.factory())
            .put(long.class, PrimitiveMemberVisitor.factory())
            .put(Long.class, PrimitiveMemberVisitor.factory())
            .put(byte.class, PrimitiveMemberVisitor.factory())
            .put(Byte.class, PrimitiveMemberVisitor.factory())
            .put(boolean.class, PrimitiveMemberVisitor.factory())
            .put(Boolean.class, PrimitiveMemberVisitor.factory())
            .put(float.class, PrimitiveMemberVisitor.factory())
            .put(Float.class, PrimitiveMemberVisitor.factory())
            .put(String.class, PrimitiveMemberVisitor.factory())
            .put(Date.class, DateMemberVisitor.factory())
            .put(Object.class, ObjectMemberVisitor.factory())
            .put(ResolvedProperty.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedObjectType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedPrimitiveType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedArrayType.class, ResolvedArrayMemberVisitor.factory())
            .put(ResolvedRecursiveType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedInterfaceType.class, ResolvedTypeMemberVisitor.factory())
            .build();

    private final HashMap<String,DocumentationSchema> schemaMap = newHashMap();
    private final SchemaDescriptor descriptor;
    private final TypeResolver typeResolver;
    private final boolean returnType;
    private List<CustomSchemaGenerator> customVisitors;

    public SchemaProvider(SchemaDescriptor descriptor, TypeResolver typeResolver, boolean returnType) {
        this.descriptor = descriptor;
        this.typeResolver = typeResolver;
        this.returnType = returnType;
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedField field) {
        ResolvedFieldInfo memberInfo = new ResolvedFieldInfo(field);
        return findKey(field).apply(this).schema(memberInfo);
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedProperty property) {
        return findKey(property).apply(this).schema(property);
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedType resolvedType) {
        ResolvedTypeMemberSource memberSource = new ResolvedTypeMemberSource(resolvedType);
        return findKey(resolvedType).apply(this).schema(memberSource);
    }

    private Function<SchemaProvider,MemberVisitor> findKey(Type returnType) {
        if (returnType instanceof ResolvedType) {
            if (any(customVisitors, thatSupports((ResolvedType) returnType))) {
                return find(customVisitors, thatSupports((ResolvedType) returnType)).factory();
            }
        }
        if (propertySchemas.containsKey(returnType.getClass())) {
            return propertySchemas.get(returnType.getClass());
        }
        throw new UnsupportedOperationException();
    }

    private Function<SchemaProvider,MemberVisitor> findKey(ResolvedField field) {
        if (any(customVisitors, thatSupports(field.getType()))){
            return find(customVisitors, thatSupports(field.getType())).factory();
        } else if (propertySchemas.containsKey(field.getRawMember().getType())) {
            return propertySchemas.get(field.getRawMember().getType());
        }
        return propertySchemas.get(field.getType().getClass());
    }

    private Function<SchemaProvider,MemberVisitor> findKey(ResolvedProperty property) {
        if (any(customVisitors, thatSupports(property.getResolvedType()))){
            return find(customVisitors, thatSupports(property.getResolvedType())).factory();
        } else if (property.getResolvedType().isPrimitive() || propertySchemas.containsKey(property.getType())) {
            return propertySchemas.get(property.getType());
        }
        return propertySchemas.get(property.getResolvedType().getClass());
    }

    private Predicate<? super CustomSchemaGenerator> thatSupports(final ResolvedType type) {
        return new Predicate<CustomSchemaGenerator>() {
            @Override
            public boolean apply(CustomSchemaGenerator input) {
                return input.supports(type);
            }
        };
    }

    public HashMap<String, DocumentationSchema> getSchemaMap() {
        return schemaMap;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public boolean isReturnType() {
        return returnType;
    }

    public List<ResolvedField> getResolvedFields(ResolvedType resolvedType) {
        if (isReturnType()) {
            return descriptor.serializableFields(this.getTypeResolver(), resolvedType);
        } else {
            return descriptor.deserializableFields(this.getTypeResolver(), resolvedType);
        }
    }

    public List<ResolvedProperty> getResolvedProperties(ResolvedType resolvedType) {
        if (isReturnType()) {
            return descriptor.serializableProperties(this.getTypeResolver(), resolvedType);
        } else {
            return descriptor.deserializableProperties(this.getTypeResolver(), resolvedType);
        }
    }

    public void setCustomVisitors(List<CustomSchemaGenerator> customVisitors) {
        this.customVisitors = customVisitors;
    }

    public boolean hasCustomSchemaGenerator(ResolvedType resolvedType) {
        return any(customVisitors, thatSupports(resolvedType));
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema generateCustomSchema(ResolvedType resolvedType) {
        return findKey(resolvedType.getErasedType()).apply(this).schema(new ResolvedTypeMemberSource(resolvedType));
    }
}
