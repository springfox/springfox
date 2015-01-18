package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.LoginEndpoint;
import com.mangofactory.documentation.service.model.ImplicitGrant;

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