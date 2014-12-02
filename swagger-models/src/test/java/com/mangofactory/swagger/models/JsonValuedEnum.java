package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JsonValuedEnum {
  ONE("One"),
  TWO("Two");
  private final String name;

  JsonValuedEnum(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
