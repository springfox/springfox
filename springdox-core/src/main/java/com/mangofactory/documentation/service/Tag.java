package com.mangofactory.documentation.service;

public class Tag {
  private final String name;
  private final String description;

  public Tag(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
