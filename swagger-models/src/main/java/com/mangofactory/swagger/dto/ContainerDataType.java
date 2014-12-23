package com.mangofactory.swagger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "items"})
public class ContainerDataType implements SwaggerDataType {
  private String type = "array";
  private boolean uniqueItems;
  @JsonProperty
  private SwaggerDataType items;

  public ContainerDataType() {
  }

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

  public void setType(String type) {
    this.type = type;
  }

  public void setUniqueItems(boolean uniqueItems) {
    this.uniqueItems = uniqueItems;
  }

  public void setItems(SwaggerDataType items) {
    this.items = items;
  }
}
