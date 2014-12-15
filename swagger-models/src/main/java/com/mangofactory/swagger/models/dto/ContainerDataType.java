package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "items"})
public class ContainerDataType implements SwaggerDataType {
  private final String type = "array";
  private final boolean uniqueItems;
  @JsonProperty
  private final SwaggerDataType items;

  public ContainerDataType(String innerType, boolean uniqueItems) {
    if (null != innerType && innerType.equals("array")) {
      throw new IllegalArgumentException("Nested arrays not supported");
    }
    items = new DataType(innerType);
    this.uniqueItems = uniqueItems;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
