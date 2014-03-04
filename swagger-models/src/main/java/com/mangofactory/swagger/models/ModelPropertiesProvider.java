package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public interface ModelPropertiesProvider {
    Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType type);
    Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type);
}
