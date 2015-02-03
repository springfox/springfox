package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.AuthorizationScope;
import com.mangofactory.documentation.service.Authorization;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

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