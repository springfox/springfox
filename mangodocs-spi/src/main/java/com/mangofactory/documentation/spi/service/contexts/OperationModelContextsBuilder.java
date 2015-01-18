package com.mangofactory.documentation.spi.service.contexts;

import com.google.common.collect.ImmutableSet;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.AlternateTypeProvider;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

public class OperationModelContextsBuilder {
  private final DocumentationType documentationType;
  private final AlternateTypeProvider alternateTypeProvider;
  private final Set<ModelContext> contexts = newHashSet();

  public OperationModelContextsBuilder(DocumentationType documentationType, AlternateTypeProvider alternateTypeProvider) {
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  public OperationModelContextsBuilder addReturn(Type type) {
    ModelContext returnValue = returnValue(type, documentationType, alternateTypeProvider);
    this.contexts.add(returnValue);
    return this;
  }

  public OperationModelContextsBuilder addInputParam(Type type) {
    ModelContext inputParam = ModelContext.inputParam(type, documentationType, alternateTypeProvider);
    this.contexts.add(inputParam);
    return this;
  }

  public Set<ModelContext> build() {
    return ImmutableSet.copyOf(contexts);
  }
}
