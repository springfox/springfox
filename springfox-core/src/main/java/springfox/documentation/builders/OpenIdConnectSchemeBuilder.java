package springfox.documentation.builders;

import springfox.documentation.service.OpenIdConnectScheme;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;

public class OpenIdConnectSchemeBuilder {
  private String name;
  private String description;
  private final List<VendorExtension> extensions = new ArrayList<>();
  private String openIdConnectUrl;

  public OpenIdConnectSchemeBuilder name(String name) {
    this.name = name;
    return this;
  }

  public OpenIdConnectSchemeBuilder description(String description) {
    this.description = description;
    return this;
  }

  public OpenIdConnectSchemeBuilder extensions(List<VendorExtension> extensions) {
    this.extensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  public OpenIdConnectSchemeBuilder openIdConnectUrl(String openIdConnectUrl) {
    this.openIdConnectUrl = openIdConnectUrl;
    return this;
  }

  public OpenIdConnectScheme build() {
    return new OpenIdConnectScheme(name, description, extensions, openIdConnectUrl);
  }
}