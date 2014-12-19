package com.mangofactory.swagger.models.dto.builder;

import com.mangofactory.swagger.models.dto.TokenEndpoint;

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