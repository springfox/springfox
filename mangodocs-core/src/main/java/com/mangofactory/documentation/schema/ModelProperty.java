package com.mangofactory.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.service.AllowableValues;

public class ModelProperty {
  private final String name;
  private final ResolvedType type;
  private final String typeName;
  private final String qualifiedType;
  private final int position;
  private final Boolean required;
  private final String description;
  private final AllowableValues allowableValues;
  private final ModelRef modelRef;

  public ModelProperty(String name, ResolvedType type, String typeName, String qualifiedType,
                       int position, Boolean required, String description, AllowableValues allowableValues,
                       ModelRef modelRef) {
    this.name = name;
    this.type = type;
    this.typeName = typeName;
    this.qualifiedType = qualifiedType;
    this.position = position;
    this.required = required;
    this.description = description;
    this.allowableValues = allowableValues;
    this.modelRef = modelRef;
  }

  public String getName() {
    return name;
  }

  public ResolvedType getType() {
    return type;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public int getPosition() {
    return position;
  }

  public Boolean isRequired() {
    return required;
  }

  public String getDescription() {
    return description;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public ModelRef getModelRef() {
    return modelRef;
  }
}
