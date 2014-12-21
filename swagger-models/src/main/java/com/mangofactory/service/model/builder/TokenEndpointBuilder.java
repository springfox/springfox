package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.TokenEndpoint;

public class TokenEndpointBuilder {
  private String url;
  private String tokenName;

  public TokenEndpointBuilder url(String url) {
    this.url = url;
    return this;
  }

  public TokenEndpointBuilder tokenName(String tokenName) {
    this.tokenName = tokenName;
    return this;
  }

  public TokenEndpoint build() {
    return new TokenEndpoint(url, tokenName);
  }
}