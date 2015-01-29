package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.AuthorizationScope;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

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