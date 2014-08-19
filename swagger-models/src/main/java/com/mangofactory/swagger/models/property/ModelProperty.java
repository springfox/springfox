package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.models.ModelContext;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;

public interface ModelProperty {
  String getName();

  ResolvedType getType();

  String typeName(ModelContext modelContext);

  String qualifiedTypeName();

  AllowableValues allowableValues();

  Option<String> propertyDescription();

  boolean isRequired();
}
