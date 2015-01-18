package com.mangofactory.documentation.swagger.dto;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class Authorization {
  private String type;
  private List<AuthorizationScope> scopes;

  public Authorization() {
  }

  public Authorization(String type, AuthorizationScope[] scopes) {
    this.scopes = newArrayList(scopes);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public void setScopes(List<AuthorizationScope> scopes) {
    this.scopes = newArrayList(scopes);
  }
}
