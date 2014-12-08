package com.mangofactory.swagger.models.dto;

public class ApiListingReference {
  private final String path;
  private final String description;
  private final int position;

  public ApiListingReference(String path, String description, int position) {
    this.path = path;
    this.description = description;
    this.position = position;
  }

  public String getPath() {
    return path;
  }

  public String getDescription() {
    return description;
  }

  public int getPosition() {
    return position;
  }
}
