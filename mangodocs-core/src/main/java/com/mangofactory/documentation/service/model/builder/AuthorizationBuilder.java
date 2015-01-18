package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.AuthorizationScope;
import com.mangofactory.documentation.service.model.Authorization;

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