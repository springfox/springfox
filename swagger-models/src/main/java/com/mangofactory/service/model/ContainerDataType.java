package com.mangofactory.service.model;

public class ContainerDataType implements SwaggerDataType {
  private final String type = "array";
  private final boolean uniqueItems;
  private final SwaggerDataType items;

  public ContainerDataType(String innerType, boolean uniqueItems) {
    if (null != innerType && innerType.equals("array")) {
      throw new IllegalArgumentException("Nested arrays not supported");
    }
    items = new DataType(innerType);
    this.uniqueItems = uniqueItems;
  }

  public String getType() {
    return type;
  }

  public boolean isUniqueItems() {
    return uniqueItems;
  }

  public SwaggerDataType getItems() {
    return items;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
