package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.ModelContext;
import scala.Option;

public interface ModelProperty {
  String getName();

  ResolvedType getType();

  String typeName(ModelContext modelContext);

  String qualifiedTypeName();

  Optional<java.util.List<String>> allowableValues();

  Option<String> propertyDescription();

  boolean isRequired();
}
