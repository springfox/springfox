package com.mangofactory.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ModelRef implements SwaggerDataType {
  private final SwaggerDataType type;


  public ModelRef(String type) {
    this.type = new DataType(type);
  }

  @Override
  public String getAbsoluteType() {
    return type.getAbsoluteType();
  }

  @JsonIgnore
  public SwaggerDataType getType() {
    return type;
  }
}
