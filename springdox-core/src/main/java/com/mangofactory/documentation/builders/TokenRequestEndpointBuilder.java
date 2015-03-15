package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.TokenRequestEndpoint;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class TokenRequestEndpointBuilder {
  private String url;
  private String clientIdName;
  private String clientSecretName;

  public TokenRequestEndpointBuilder url(String url) {
    this.url = defaultIfAbsent(url, this.url);
    return this;
  }

  public TokenRequestEndpointBuilder clientIdName(String clientIdName) {
    this.clientIdName = defaultIfAbsent(clientIdName, this.clientIdName);
    return this;
  }

  public TokenRequestEndpointBuilder clientSecretName(String clientSecretName) {
    this.clientSecretName = defaultIfAbsent(clientSecretName, this.clientSecretName);
    return this;
  }

  public TokenRequestEndpoint build() {
    return new TokenRequestEndpoint(url, clientIdName, clientSecretName);
  }
}