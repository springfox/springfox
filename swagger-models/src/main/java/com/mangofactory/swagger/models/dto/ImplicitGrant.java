package com.mangofactory.swagger.models.dto;


public class ImplicitGrant extends GrantType {
  private final LoginEndpoint loginEndpoint;
  private final String tokenName;

  public ImplicitGrant(LoginEndpoint loginEndpoint, String tokenName) {
    super("implicit");
    this.loginEndpoint = loginEndpoint;
    this.tokenName = tokenName;
  }

  public LoginEndpoint getLoginEndpoint() {
    return loginEndpoint;
  }

  public String getTokenName() {
    return tokenName;
  }
}
