package com.mangofactory.swagger.dto;

public class GrantType {
  private final String type;

  public GrantType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
