package com.mangofactory.documentation.builder;

import com.mangofactory.documentation.service.model.TokenRequestEndpoint;

import static com.mangofactory.documentation.builder.BuilderDefaults.*;

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