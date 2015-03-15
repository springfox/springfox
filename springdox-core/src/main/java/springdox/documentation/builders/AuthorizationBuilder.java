package springdox.documentation.builders;

import springdox.documentation.service.Authorization;
import springdox.documentation.service.AuthorizationScope;

import static springdox.documentation.builders.BuilderDefaults.*;

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