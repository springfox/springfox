package springdox.documentation.builders;

import springdox.documentation.service.AuthorizationScope;

public class AuthorizationScopeBuilder {
  private String scope;
  private String description;

  public AuthorizationScopeBuilder scope(String scope) {
    this.scope = BuilderDefaults.defaultIfAbsent(scope, this.scope);
    return this;
  }

  public AuthorizationScopeBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public AuthorizationScope build() {
    return new AuthorizationScope(scope, description);
  }
}