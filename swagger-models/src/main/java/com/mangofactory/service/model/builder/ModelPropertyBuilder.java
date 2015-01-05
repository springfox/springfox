package com.mangofactory.service.model.builder;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.service.model.ModelProperty;
import com.mangofactory.service.model.ModelRef;

public class ModelPropertyBuilder {
  private ResolvedType type;
  private String qualifiedType;
  private int position;
  private Boolean required;
  private String description;
  private AllowableValues allowableValues;
  private ModelRef items;
  private String name;

  public ModelPropertyBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ModelPropertyBuilder type(ResolvedType type) {
    this.type = type;
    return this;
  }

  public ModelPropertyBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
    return this;
  }

  public ModelPropertyBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ModelPropertyBuilder required(Boolean required) {
    this.required = required;
    return this;
  }

  public ModelPropertyBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ModelPropertyBuilder allowableValues(AllowableValues allowableValues) {
    //Preference to inferred allowable values over list values via ApiModelProperty
    if (this.allowableValues == null && !allowableValuesIsEmpty(allowableValues)) {
      this.allowableValues = allowableValues;
    }
    return this;
  }

  private boolean allowableValuesIsEmpty(AllowableValues values) {
    if (values != null) {
      if (values instanceof AllowableListValues) {
        return ((AllowableListValues) values).getValues().isEmpty();
      }
      return true;
    }
    return false;
  }

  public ModelPropertyBuilder items(ModelRef items) {
    this.items = items;
    return this;
  }

  public ModelProperty build() {
    return new ModelProperty(name, type, qualifiedType, position, required, description, allowableValues, items);
  }
}