package springfox.documentation.service;

import springfox.documentation.builders.HttpAuthenticationBuilder;

import java.util.List;

public class HttpAuthenticationScheme extends SecurityScheme {
  public static final HttpAuthenticationBuilder BASIC_AUTH_BUILDER = new HttpAuthenticationBuilder().scheme("basic");
  public static final HttpAuthenticationBuilder JWT_BEARER_BUILDER = new HttpAuthenticationBuilder()
      .scheme("bearer")
      .bearerFormat("JWT");
  private final String scheme;
  private final String bearerFormat;


  public HttpAuthenticationScheme(
      String name,
      String description,
      String type,
      String scheme,
      String bearerFormat,
      List<VendorExtension> extensions) {
    super(name, type, description, extensions);
    this.scheme = scheme;
    this.bearerFormat = bearerFormat;
  }

  public String getScheme() {
    return scheme;
  }

  public String getBearerFormat() {
    return bearerFormat;
  }
}
