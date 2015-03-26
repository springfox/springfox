package springfox.documentation.builders;

import springfox.documentation.service.TokenRequestEndpoint;

public class TokenRequestEndpointBuilder {
  private String url;
  private String clientIdName;
  private String clientSecretName;

  public TokenRequestEndpointBuilder url(String url) {
    this.url = BuilderDefaults.defaultIfAbsent(url, this.url);
    return this;
  }

  public TokenRequestEndpointBuilder clientIdName(String clientIdName) {
    this.clientIdName = BuilderDefaults.defaultIfAbsent(clientIdName, this.clientIdName);
    return this;
  }

  public TokenRequestEndpointBuilder clientSecretName(String clientSecretName) {
    this.clientSecretName = BuilderDefaults.defaultIfAbsent(clientSecretName, this.clientSecretName);
    return this;
  }

  public TokenRequestEndpoint build() {
    return new TokenRequestEndpoint(url, clientIdName, clientSecretName);
  }
}