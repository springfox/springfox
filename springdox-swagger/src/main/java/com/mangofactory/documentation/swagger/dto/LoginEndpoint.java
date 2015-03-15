package com.mangofactory.documentation.swagger.dto;

public class LoginEndpoint {
  private String url;

  public LoginEndpoint() {
  }

  public LoginEndpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
