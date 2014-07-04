package com.mangofactory.swagger.models.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.models.property.ModelProperty;

public interface ModelPropertiesProvider {
    Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType type);
    Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type);
}
