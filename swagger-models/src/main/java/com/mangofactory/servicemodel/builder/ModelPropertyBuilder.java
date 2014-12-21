package com.mangofactory.servicemodel.builder;

import com.mangofactory.servicemodel.AllowableValues;
import com.mangofactory.servicemodel.ModelProperty;
import com.mangofactory.servicemodel.ModelRef;

public class ModelPropertyBuilder {
  private String type;
  private String qualifiedType;
  private int position;
  private Boolean required;
  private String description;
  private AllowableValues allowableValues;
  private ModelRef items;

  public ModelPropertyBuilder type(String type) {
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
    this.allowableValues = allowableValues;
    return this;
  }

  public ModelPropertyBuilder iItems(ModelRef items) {
    this.items = items;
    return this;
  }

  public ModelProperty build() {
    return new ModelProperty(type, qualifiedType, position, required, description, allowableValues, items);
  }
}