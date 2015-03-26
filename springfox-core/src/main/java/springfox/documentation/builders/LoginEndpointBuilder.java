package springfox.documentation.builders;

import springfox.documentation.service.LoginEndpoint;

public class LoginEndpointBuilder {
  private String url;

  public LoginEndpointBuilder url(String url) {
    this.url = BuilderDefaults.defaultIfAbsent(url, this.url);
    return this;
  }

  public LoginEndpoint build() {
    return new LoginEndpoint(url);
  }
}