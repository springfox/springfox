package com.mangofactory.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.service.AllowableValues;

public interface ModelProperty {
  String getName();

  ResolvedType getType();

  String qualifiedTypeName();

  AllowableValues allowableValues();

  String propertyDescription();

  boolean isRequired();

  int position();
}
