package com.mangofactory.documentation.builder;

import com.mangofactory.documentation.service.model.AuthorizationScope;
import com.mangofactory.documentation.service.model.Authorization;

import static com.mangofactory.documentation.builder.BuilderDefaults.*;

public class AuthorizationBuilder {
  private String type;
  private AuthorizationScope[] scopes;

  public AuthorizationBuilder type(String type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  public AuthorizationBuilder scopes(AuthorizationScope[] scopes) {
    this.scopes = defaultIfAbsent(scopes, this.scopes);
    return this;
  }

  public Authorization build() {
    return new Authorization(type, scopes);
  }
}