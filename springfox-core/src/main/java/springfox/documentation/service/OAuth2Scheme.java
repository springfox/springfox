package springfox.documentation.service;

import springfox.documentation.builders.OAuth2SchemeBuilder;

import java.util.ArrayList;
import java.util.List;

public class OAuth2Scheme extends SecurityScheme {
  public static final OAuth2SchemeBuilder OAUTH2_IMPLICIT_FLOW_BUILDER
      = new OAuth2SchemeBuilder("implicit");
  public static final OAuth2SchemeBuilder OAUTH2_PASSWORD_FLOW_BUILDER
      = new OAuth2SchemeBuilder("password");
  public static final OAuth2SchemeBuilder OAUTH2_CLIENT_CREDENTIALS_FLOW_BUILDER
      = new OAuth2SchemeBuilder("clientCredentials");
  public static final OAuth2SchemeBuilder OAUTH2_AUTHORIZATION_CODE_FLOW_BUILDER
      = new OAuth2SchemeBuilder("authorizationCode");

  private final String flowType;
  private final String authorizationUrl;
  private final String tokenUrl;
  private final String refreshUrl;
  private final List<AuthorizationScope> scopes = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public OAuth2Scheme(
      String name,
      String flowType,
      String description,
      String authorizationUrl,
      String tokenUrl,
      String refreshUrl,
      List<AuthorizationScope> scopes,
      List<VendorExtension> extensions) {
    super(name, "oauth2", description, extensions);
    this.flowType = flowType;
    this.authorizationUrl = authorizationUrl;
    this.tokenUrl = tokenUrl;
    this.refreshUrl = refreshUrl;
    this.scopes.addAll(scopes);
  }

  public String getFlowType() {
    return flowType;
  }

  public String getAuthorizationUrl() {
    return authorizationUrl;
  }

  public String getTokenUrl() {
    return tokenUrl;
  }

  public String getRefreshUrl() {
    return refreshUrl;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }
}
