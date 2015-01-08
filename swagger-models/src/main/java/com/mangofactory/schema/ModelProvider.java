package com.mangofactory.schema;

import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.service.model.Model;

public interface ModelProvider {
  com.google.common.base.Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<String, Model> dependencies(ModelContext modelContext);
}
