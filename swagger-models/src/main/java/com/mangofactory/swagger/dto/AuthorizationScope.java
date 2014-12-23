package com.mangofactory.swagger.dto;

public class AuthorizationScope {
  private String scope;
  private String description;

  public AuthorizationScope() {
  }

  public AuthorizationScope(String scope, String description) {
    this.description = description;
    this.scope = scope;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
