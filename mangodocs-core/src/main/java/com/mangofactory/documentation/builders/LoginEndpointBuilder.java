package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.model.LoginEndpoint;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class LoginEndpointBuilder {
  private String url;

  public LoginEndpointBuilder url(String url) {
    this.url = defaultIfAbsent(url, this.url);
    return this;
  }

  public LoginEndpoint build() {
    return new LoginEndpoint(url);
  }
}