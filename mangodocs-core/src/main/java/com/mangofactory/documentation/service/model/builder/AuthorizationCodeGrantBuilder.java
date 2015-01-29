package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.TokenRequestEndpoint;
import com.mangofactory.documentation.service.model.AuthorizationCodeGrant;
import com.mangofactory.documentation.service.model.TokenEndpoint;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class AuthorizationCodeGrantBuilder {
  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrantBuilder tokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = defaultIfAbsent(tokenRequestEndpoint, this.tokenRequestEndpoint);
    return this;
  }

  public AuthorizationCodeGrantBuilder tokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = defaultIfAbsent(tokenEndpoint, this.tokenEndpoint);
    return this;
  }

  public AuthorizationCodeGrant build() {
    return new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
  }
}