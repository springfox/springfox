package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.ImplicitGrant;
import com.mangofactory.service.model.LoginEndpoint;

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