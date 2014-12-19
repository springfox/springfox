package com.mangofactory.swagger.models.dto.builder;

import com.mangofactory.swagger.models.dto.Authorization;
import com.mangofactory.swagger.models.dto.AuthorizationScope;

public class AuthorizationBuilder {
  private String type;
  private AuthorizationScope[] scopes;

  public AuthorizationBuilder type(String type) {
    this.type = type;
    return this;
  }

  public AuthorizationBuilder scopes(AuthorizationScope[] scopes) {
    this.scopes = scopes;
    return this;
  }

  public Authorization build() {
    return new Authorization(type, scopes);
  }
}