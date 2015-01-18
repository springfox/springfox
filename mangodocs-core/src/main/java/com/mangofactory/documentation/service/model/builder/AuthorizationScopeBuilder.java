package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.AuthorizationScope;

public class AuthorizationScopeBuilder {
  private String scope;
  private String description;

  public AuthorizationScopeBuilder scope(String scope) {
    this.scope = scope;
    return this;
  }

  public AuthorizationScopeBuilder description(String description) {
    this.description = description;
    return this;
  }

  public AuthorizationScope build() {
    return new AuthorizationScope(scope, description);
  }
}