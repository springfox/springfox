package com.mangofactory.swagger.models.dto;


public class PrimitiveFormatParameterType implements ParameterType {
  private final String type;
  private final String format;

  public PrimitiveFormatParameterType(String type, String format) {
    this.type = type;
    this.format = format;
  }

  public String getType() {
    return type;
  }

  public String getFormat() {
    return format;
  }
}
