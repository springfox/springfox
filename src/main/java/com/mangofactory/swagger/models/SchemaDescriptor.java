package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.AliasedResolvedField;

import java.util.List;

public interface SchemaDescriptor {

    List<AliasedResolvedField> serializableFields(TypeResolver typeResolver, ResolvedType resolvedType);

    List<AliasedResolvedField> deserializableFields(TypeResolver typeResolver, ResolvedType resolvedType);

    List<ResolvedPropertyInfo> serializableProperties(TypeResolver typeResolver, ResolvedType resolvedType);

    List<ResolvedPropertyInfo> deserializableProperties(TypeResolver typeResolver, ResolvedType resolvedType);

}

