package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.AuthorizationCodeGrant;
import com.mangofactory.service.model.TokenEndpoint;
import com.mangofactory.service.model.TokenRequestEndpoint;

public class AuthorizationCodeGrantBuilder {
  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrantBuilder tokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = tokenRequestEndpoint;
    return this;
  }

  public AuthorizationCodeGrantBuilder tokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
    return this;
  }

  public AuthorizationCodeGrant build() {
    return new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
  }
}