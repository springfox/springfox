package com.mangofactory.spring.web.plugins;

import com.google.common.collect.ImmutableSet;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.schema.plugins.ModelContext;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class OperationModelsBuilder {
  private final DocumentationType documentationType;
  private final Set<ModelContext> contexts = newHashSet();

  public OperationModelsBuilder(DocumentationType documentationType) {
    this.documentationType = documentationType;
  }

  public void addReturn(Type type) {
    ModelContext returnValue = ModelContext.returnValue(type, documentationType);
    this.contexts.add(returnValue);
  }

  public void addInputParam(Type type) {
    ModelContext inputParam = ModelContext.inputParam(type, documentationType);
    this.contexts.add(inputParam);
  }

  public Set<ModelContext> build() {
    return ImmutableSet.copyOf(contexts);
  }
}
