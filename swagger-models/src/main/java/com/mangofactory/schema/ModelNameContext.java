package com.mangofactory.schema;

import com.mangofactory.schema.plugins.DocumentationType;

public class ModelNameContext {
  private final Class<?> type;
  private final DocumentationType documentationType;

  public ModelNameContext(Class<?> type, DocumentationType documentationType) {
    this.type = type;
    this.documentationType = documentationType;
  }

  public Class<?> getType() {
    return type;
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }
}
