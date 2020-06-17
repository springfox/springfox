package springfox.documentation.service;

import java.util.List;

public class OpenIdConnectScheme extends SecurityScheme {
  private final String openIdConnectUrl;

  public OpenIdConnectScheme(
      String name,
      String description,
      List<VendorExtension> extensions,
      String openIdConnectUrl) {
    super(name, "openIdConnect", description, extensions);
    this.openIdConnectUrl = openIdConnectUrl;
  }

  public String getOpenIdConnectUrl() {
    return openIdConnectUrl;
  }
}
