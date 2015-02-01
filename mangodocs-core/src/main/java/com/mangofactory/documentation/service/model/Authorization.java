package com.mangofactory.documentation.service.model;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class Authorization {
  private final String type;
  private final List<AuthorizationScope> scopes;

  public Authorization(String type, AuthorizationScope[] scopes) {
    this.scopes = newArrayList(scopes);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }
}
