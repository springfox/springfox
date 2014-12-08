package com.mangofactory.swagger.models.dto;

public class PrimitiveParameterType implements ParameterType {
  private final String type;

  public PrimitiveParameterType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
