package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.LoginEndpoint;

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