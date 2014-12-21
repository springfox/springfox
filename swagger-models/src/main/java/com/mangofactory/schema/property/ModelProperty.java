package com.mangofactory.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.schema.ModelContext;
import com.mangofactory.service.model.AllowableValues;

public interface ModelProperty {
  String getName();

  ResolvedType getType();

  String typeName(ModelContext modelContext);

  String qualifiedTypeName();

  AllowableValues allowableValues();

  String propertyDescription();

  boolean isRequired();

  int position();
}
