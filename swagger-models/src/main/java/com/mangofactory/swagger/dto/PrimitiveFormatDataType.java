package com.mangofactory.swagger.dto;


public class PrimitiveFormatDataType implements SwaggerDataType {
  private String type;
  private String format;

  public PrimitiveFormatDataType() {
  }

  public PrimitiveFormatDataType(String type, String format) {
    this.type = type;
    this.format = format;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
