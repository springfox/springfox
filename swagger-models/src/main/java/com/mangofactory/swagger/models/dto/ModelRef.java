package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ModelRef implements SwaggerDataType{
  @JsonProperty
  @JsonUnwrapped
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
