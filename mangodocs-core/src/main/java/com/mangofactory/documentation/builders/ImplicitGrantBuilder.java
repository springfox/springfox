package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.LoginEndpoint;
import com.mangofactory.documentation.service.ImplicitGrant;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ImplicitGrantBuilder {
  private LoginEndpoint loginEndpoint;
  private String tokenName;

  public ImplicitGrantBuilder loginEndpoint(LoginEndpoint loginEndpoint) {
    this.loginEndpoint = defaultIfAbsent(loginEndpoint, this.loginEndpoint);
    return this;
  }

  public ImplicitGrantBuilder tokenName(String tokenName) {
    this.tokenName = defaultIfAbsent(tokenName, this.tokenName);
    return this;
  }

  public ImplicitGrant build() {
    return new ImplicitGrant(loginEndpoint, tokenName);
  }
}