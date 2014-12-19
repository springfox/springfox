package com.mangofactory.swagger.models.dto.builder;

import com.mangofactory.swagger.models.dto.ImplicitGrant;
import com.mangofactory.swagger.models.dto.LoginEndpoint;

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