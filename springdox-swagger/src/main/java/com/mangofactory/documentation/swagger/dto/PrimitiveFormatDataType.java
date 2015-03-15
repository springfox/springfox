package com.mangofactory.documentation.swagger.dto;


public class PrimitiveFormatDataType implements SwaggerDataType {
  private String type;
  private String format;

  public PrimitiveFormatDataType(String type, String format) {
    this.type = type;
    this.format = format;
  }

  public String getType() {
    return type;
  }

  public String getFormat() {
    return format;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
