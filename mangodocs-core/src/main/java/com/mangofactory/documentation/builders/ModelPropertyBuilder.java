package com.mangofactory.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.service.model.AllowableListValues;
import com.mangofactory.documentation.service.model.AllowableValues;

import static com.mangofactory.documentation.schema.Enums.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ModelPropertyBuilder {
  private ResolvedType type;
  private String qualifiedType;
  private int position;
  private Boolean required;
  private String description;
  private AllowableValues allowableValues;
  private ModelRef items;
  private String name;
  private String typeName;

  public ModelPropertyBuilder name(String name) {
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  public ModelPropertyBuilder type(ResolvedType type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  public ModelPropertyBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = defaultIfAbsent(qualifiedType, this.qualifiedType);
    return this;
  }

  public ModelPropertyBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ModelPropertyBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  public ModelPropertyBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  public ModelPropertyBuilder allowableValues(AllowableValues allowableValues) {
    if (allowableValues != null) {
      if (allowableValues instanceof AllowableListValues) {
        this.allowableValues = emptyListValuesToNull((AllowableListValues) allowableValues);
      } else {
        this.allowableValues = allowableValues;
      }
    }
    return this;
  }

  public ModelPropertyBuilder typeName(String typeName) {
    this.typeName = defaultIfAbsent(typeName, this.typeName);
    return this;
  }

  public ModelPropertyBuilder items(ModelRef items) {
    this.items = defaultIfAbsent(items, this.items);
    return this;
  }

  public ModelProperty build() {
    return new ModelProperty(name, type, typeName, qualifiedType, position, required, description, allowableValues,
            items);
  }

}