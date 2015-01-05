package com.mangofactory.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.schema.ModelContext;
import com.mangofactory.service.model.ModelProperty;

import java.util.List;

public interface ModelPropertiesProvider {
//  List<com.mangofactory.service.model.ModelProperty> propertiesForSerialization(ResolvedType type);
//
//  List<com.mangofactory.service.model.ModelProperty> propertiesForDeserialization(ResolvedType type);

  List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);
  void setObjectMapper(ObjectMapper objectMapper);
}
