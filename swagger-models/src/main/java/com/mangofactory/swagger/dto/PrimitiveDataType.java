package com.mangofactory.swagger.dto;

public class PrimitiveDataType implements SwaggerDataType {
  private String type;

  public PrimitiveDataType() {
  }

  public PrimitiveDataType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
