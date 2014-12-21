package com.mangofactory.servicemodel.builder;

import com.mangofactory.servicemodel.ImplicitGrant;
import com.mangofactory.servicemodel.LoginEndpoint;

public class ImplicitGrantBuilder {
  private LoginEndpoint loginEndpoint;
  private String tokenName;

  public ImplicitGrantBuilder loginEndpoint(LoginEndpoint loginEndpoint) {
    this.loginEndpoint = loginEndpoint;
    return this;
  }

  public ImplicitGrantBuilder tokenName(String tokenName) {
    this.tokenName = tokenName;
    return this;
  }

  public ImplicitGrant build() {
    return new ImplicitGrant(loginEndpoint, tokenName);
  }
}