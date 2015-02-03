package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.AuthorizationScope;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class AuthorizationScopeBuilder {
  private String scope;
  private String description;

  public AuthorizationScopeBuilder scope(String scope) {
    this.scope = defaultIfAbsent(scope, this.scope);
    return this;
  }

  public AuthorizationScopeBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  public AuthorizationScope build() {
    return new AuthorizationScope(scope, description);
  }
}