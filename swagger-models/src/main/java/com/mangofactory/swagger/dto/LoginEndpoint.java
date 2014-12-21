package com.mangofactory.swagger.dto;

public class LoginEndpoint {
  private final String url;

  public LoginEndpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
