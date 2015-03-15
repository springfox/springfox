package com.mangofactory.documentation.schema;


import com.mangofactory.documentation.spi.schema.contexts.ModelContext;

public interface ModelProvider {
  com.google.common.base.Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<String, Model> dependencies(ModelContext modelContext);
}
