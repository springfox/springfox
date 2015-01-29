package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.TokenEndpoint;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class TokenEndpointBuilder {
  private String url;
  private String tokenName;

  public TokenEndpointBuilder url(String url) {
    this.url = defaultIfAbsent(url, this.url);
    return this;
  }

  public TokenEndpointBuilder tokenName(String tokenName) {
    this.tokenName = defaultIfAbsent(tokenName, this.tokenName);
    return this;
  }

  public TokenEndpoint build() {
    return new TokenEndpoint(url, tokenName);
  }
}