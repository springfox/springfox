package com.mangofactory.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.service.model.AllowableValues;

public interface ModelProperty {
  String getName();

  ResolvedType getType();

  String qualifiedTypeName();

  AllowableValues allowableValues();

  String propertyDescription();

  boolean isRequired();

  int position();
}
