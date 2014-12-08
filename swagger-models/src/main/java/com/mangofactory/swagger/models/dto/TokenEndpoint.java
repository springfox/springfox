package com.mangofactory.swagger.models.dto;

public class TokenEndpoint {

  private final String url;
  private final String tokenName;

  public TokenEndpoint(String url, String tokenName) {
    this.url = url;
    this.tokenName = tokenName;
  }

  public String getUrl() {
    return url;
  }

  public String getTokenName() {
    return tokenName;
  }
}
