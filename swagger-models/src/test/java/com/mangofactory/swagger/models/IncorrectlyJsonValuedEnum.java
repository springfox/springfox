package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IncorrectlyJsonValuedEnum {
  ONE("One"),
  TWO("Two");

  private final String name;

  IncorrectlyJsonValuedEnum(String name) {
    this.name = name;
  }

  @JsonValue
  public String setName(String someName) {
    return name;
  }
}
