package springdox.documentation.builders;

import springdox.documentation.service.TokenEndpoint;

public class TokenEndpointBuilder {
  private String url;
  private String tokenName;

  public TokenEndpointBuilder url(String url) {
    this.url = BuilderDefaults.defaultIfAbsent(url, this.url);
    return this;
  }

  public TokenEndpointBuilder tokenName(String tokenName) {
    this.tokenName = BuilderDefaults.defaultIfAbsent(tokenName, this.tokenName);
    return this;
  }

  public TokenEndpoint build() {
    return new TokenEndpoint(url, tokenName);
  }
}