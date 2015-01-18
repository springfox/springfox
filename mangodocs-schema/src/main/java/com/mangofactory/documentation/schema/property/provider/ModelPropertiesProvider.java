package com.mangofactory.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;

import java.util.List;

public interface ModelPropertiesProvider {
  List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);
  void setObjectMapper(ObjectMapper objectMapper);
}
