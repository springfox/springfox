package com.mangofactory.swagger.models.servicemodel.builder;

import com.mangofactory.swagger.models.servicemodel.AuthorizationCodeGrant;
import com.mangofactory.swagger.models.servicemodel.TokenEndpoint;
import com.mangofactory.swagger.models.servicemodel.TokenRequestEndpoint;

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