package com.mangofactory.swagger.models.dto;

public class TokenRequestEndpoint {

  private final String url;
  private final String clientIdName;
  private final String  clientSecretName;

  public TokenRequestEndpoint(String url, String clientIdName, String clientSecretName) {
    this.url = url;
    this.clientIdName = clientIdName;
    this.clientSecretName = clientSecretName;
  }

  public String getUrl() {
    return url;
  }

  public String getClientIdName() {
    return clientIdName;
  }

  public String getClientSecretName() {
    return clientSecretName;
  }
}
