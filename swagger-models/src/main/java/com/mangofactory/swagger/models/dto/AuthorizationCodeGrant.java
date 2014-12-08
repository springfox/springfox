package com.mangofactory.swagger.models.dto;

public class AuthorizationCodeGrant extends GrantType {

  private final TokenRequestEndpoint tokenRequestEndpoint;
  private final TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrant(TokenRequestEndpoint tokenRequestEndpoint, TokenEndpoint tokenEndpoint) {
    super("authorization_code");
    this.tokenRequestEndpoint = tokenRequestEndpoint;
    this.tokenEndpoint = tokenEndpoint;
  }

  public TokenRequestEndpoint getTokenRequestEndpoint() {
    return tokenRequestEndpoint;
  }

  public TokenEndpoint getTokenEndpoint() {
    return tokenEndpoint;
  }
}
