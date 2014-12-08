package com.mangofactory.swagger.models.dto;

public class AuthorizationScope {
  private final String scope;
  private final String description;

  public AuthorizationScope(String scope, String description) {
    this.description = description;
    this.scope = scope;
  }

  public String getScope() {
    return scope;
  }

  public String getDescription() {
    return description;
  }
}
