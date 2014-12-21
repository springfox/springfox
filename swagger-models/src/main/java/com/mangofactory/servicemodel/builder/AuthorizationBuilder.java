package com.mangofactory.servicemodel.builder;

import com.mangofactory.servicemodel.Authorization;
import com.mangofactory.servicemodel.AuthorizationScope;

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