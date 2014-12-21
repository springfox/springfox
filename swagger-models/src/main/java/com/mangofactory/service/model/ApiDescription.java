package com.mangofactory.service.model;

import java.util.List;

public class ApiDescription {
  private final String path;
  private final String description;
  private final List<Operation> operations;
  private final Boolean hidden;

  public ApiDescription(String path, String description, List<Operation> operations, Boolean hidden) {
    this.path = path;
    this.description = description;
    this.operations = operations;
    this.hidden = hidden;
  }

  public String getPath() {
    return path;
  }

  public String getDescription() {
    return description;
  }

  public List<Operation> getOperations() {
    return operations;
  }

  public Boolean isHidden() {
    return hidden;
  }
}
