package com.mangofactory.documentation.service.model;

import com.google.common.collect.Lists;

import java.util.List;

public class Authorization {
  private final String type;
  private final List<AuthorizationScope> scopes;

  public Authorization(String type, AuthorizationScope[] scopes) {
    this.scopes = Lists.newArrayList(scopes);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }
}
