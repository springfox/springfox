package com.mangofactory.swagger.models.servicemodel;

public class PrimitiveDataType implements SwaggerDataType {
  private final String type;

  public PrimitiveDataType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
