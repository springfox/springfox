package springfox.documentation.builders;

import springfox.documentation.service.Authorization;
import springfox.documentation.service.AuthorizationScope;

public class AuthorizationBuilder {
  private String type;
  private AuthorizationScope[] scopes;

  public AuthorizationBuilder type(String type) {
    this.type = BuilderDefaults.defaultIfAbsent(type, this.type);
    return this;
  }

  public AuthorizationBuilder scopes(AuthorizationScope[] scopes) {
    this.scopes = BuilderDefaults.defaultIfAbsent(scopes, this.scopes);
    return this;
  }

  public Authorization build() {
    return new Authorization(type, scopes);
  }
}