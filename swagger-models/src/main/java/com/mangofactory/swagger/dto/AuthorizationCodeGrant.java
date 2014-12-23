package com.mangofactory.swagger.dto;

public class AuthorizationCodeGrant extends GrantType {

  private TokenRequestEndpoint tokenRequestEndpoint;
  private TokenEndpoint tokenEndpoint;

  public AuthorizationCodeGrant() {
    super("authorization_code");
  }

  public AuthorizationCodeGrant(TokenRequestEndpoint tokenRequestEndpoint, TokenEndpoint tokenEndpoint) {
    super("authorization_code");
    this.tokenRequestEndpoint = tokenRequestEndpoint;
    this.tokenEndpoint = tokenEndpoint;
  }

  public TokenRequestEndpoint getTokenRequestEndpoint() {
    return tokenRequestEndpoint;
  }

  public void setTokenRequestEndpoint(TokenRequestEndpoint tokenRequestEndpoint) {
    this.tokenRequestEndpoint = tokenRequestEndpoint;
  }

  public TokenEndpoint getTokenEndpoint() {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(TokenEndpoint tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
  }
}
