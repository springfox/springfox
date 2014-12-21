package com.mangofactory.swagger.models.servicemodel.builder;

import com.mangofactory.swagger.models.servicemodel.LoginEndpoint;

public class LoginEndpointBuilder {
  private String url;

  public LoginEndpointBuilder url(String url) {
    this.url = url;
    return this;
  }

  public LoginEndpoint build() {
    return new LoginEndpoint(url);
  }
}