package com.mangofactory.swagger.models.dto.builder;

import com.mangofactory.swagger.models.dto.LoginEndpoint;

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