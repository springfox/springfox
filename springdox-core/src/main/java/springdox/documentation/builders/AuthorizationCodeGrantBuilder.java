package springdox.documentation.builders;

import springdox.documentation.service.AuthorizationCodeGrant;
import springdox.documentation.service.TokenEndpoint;
import springdox.documentation.service.TokenRequestEndpoint;

public class AuthorizationCodeGrantBuilder {
  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrantBuilder tokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = BuilderDefaults.defaultIfAbsent(tokenRequestEndpoint, this.tokenRequestEndpoint);
    return this;
  }

  public AuthorizationCodeGrantBuilder tokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = BuilderDefaults.defaultIfAbsent(tokenEndpoint, this.tokenEndpoint);
    return this;
  }

  public AuthorizationCodeGrant build() {
    return new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
  }
}