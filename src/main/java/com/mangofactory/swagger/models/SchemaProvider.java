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
import com.google.common.collect.ImmutableMap;
import com.mangofactory.swagger.AliasedResolvedField;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationSchema;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            .put(short.class, PrimitiveMemberVisitor.factory())
            .put(Short.class, PrimitiveMemberVisitor.factory())
            .put(byte.class, PrimitiveMemberVisitor.factory())
            .put(Byte.class, PrimitiveMemberVisitor.factory())
            .put(boolean.class, PrimitiveMemberVisitor.factory())
            .put(Boolean.class, PrimitiveMemberVisitor.factory())
            .put(float.class, PrimitiveMemberVisitor.factory())
            .put(Float.class, PrimitiveMemberVisitor.factory())
            .put(String.class, PrimitiveMemberVisitor.factory())
            .put(Date.class, DateMemberVisitor.factory())
            .put(Object.class, ObjectMemberVisitor.factory())
            .put(ResolvedPropertyInfo.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedObjectType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedPrimitiveType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedArrayType.class, ResolvedArrayMemberVisitor.factory())
            .put(ResolvedRecursiveType.class, ResolvedTypeMemberVisitor.factory())
            .put(ResolvedInterfaceType.class, ResolvedTypeMemberVisitor.factory())
            .build();

    private final HashMap<String,DocumentationSchema> schemaMap = newHashMap();
    private final SwaggerConfiguration configuration;
    private final SchemaDescriptor descriptor;
    private final TypeResolver typeResolver;
    private final boolean returnType;

    public SchemaProvider(SwaggerConfiguration configuration, SchemaDescriptor descriptor, TypeResolver typeResolver,
                          boolean returnType) {
        this.configuration = configuration;
        this.descriptor = descriptor;
        this.typeResolver = typeResolver;
        this.returnType = returnType;
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedField field) {
        ResolvedFieldInfo memberInfo = new ResolvedFieldInfo(configuration, field);
        return findKey(field).apply(this).schema(memberInfo);
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedPropertyInfo property) {
        return findKey(property).apply(this).schema(property);
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema schema(ResolvedType resolvedType) {
        ResolvedType alternate = configuration.maybeGetAlternateType(resolvedType);
        ResolvedTypeMemberSource memberSource = new ResolvedTypeMemberSource(alternate);
        return findKey(alternate).apply(this).schema(memberSource);
    }

    private Function<SchemaProvider,MemberVisitor> findKey(Type returnType) {
        if (propertySchemas.containsKey(returnType.getClass())) {
            return propertySchemas.get(returnType.getClass());
        }
        throw new UnsupportedOperationException();
    }

    private Function<SchemaProvider,MemberVisitor> findKey(ResolvedField field) {
        ResolvedType type = configuration.maybeGetAlternateType(field.getType());
        if (propertySchemas.containsKey(type.getClass())) {
            return propertySchemas.get(type.getClass());
        }
        return propertySchemas.get(type.getClass());
    }

    private Function<SchemaProvider,MemberVisitor> findKey(ResolvedPropertyInfo property) {
        ResolvedType resolvedType = configuration.maybeGetAlternateType(property.getResolvedType());
        if (resolvedType.isPrimitive() || propertySchemas.containsKey(property.getType())) {
            return propertySchemas.get(property.getType());
        }
        return propertySchemas.get(resolvedType.getClass());
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

    public List<AliasedResolvedField> getResolvedFields(ResolvedType resolvedType) {
        if (isReturnType()) {
            return descriptor.serializableFields(this.getTypeResolver(), resolvedType);
        } else {
            return descriptor.deserializableFields(this.getTypeResolver(), resolvedType);
        }
    }

    public List<ResolvedPropertyInfo> getResolvedProperties(ResolvedType resolvedType) {
        if (isReturnType()) {
            return descriptor.serializableProperties(this.getTypeResolver(), resolvedType);
        } else {
            return descriptor.deserializableProperties(this.getTypeResolver(), resolvedType);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public DocumentationSchema generateCustomSchema(ResolvedType resolvedType) {
        return findKey(resolvedType.getErasedType()).apply(this).schema(new ResolvedTypeMemberSource(resolvedType));
    }
}
