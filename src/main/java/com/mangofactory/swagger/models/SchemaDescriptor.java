package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;

import java.util.List;

public interface SchemaDescriptor {

    List<ResolvedField> serializableFields(TypeResolver typeResolver, ResolvedType resolvedType);

    List<ResolvedField> deserializableFields(TypeResolver typeResolver, ResolvedType resolvedType);

    List<ResolvedProperty> serializableProperties(TypeResolver typeResolver, ResolvedType resolvedType);

    List<ResolvedProperty> deserializableProperties(TypeResolver typeResolver, ResolvedType resolvedType);

}

