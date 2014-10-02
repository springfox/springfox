package com.mangofactory.swagger.models;

import com.wordnik.swagger.models.Model;

public interface ModelProvider {
  com.google.common.base.Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<String, com.wordnik.swagger.models.Model> dependencies(ModelContext modelContext);
}
