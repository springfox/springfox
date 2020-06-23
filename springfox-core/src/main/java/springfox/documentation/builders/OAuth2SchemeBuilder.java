package springfox.documentation.builders;

import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.OAuth2Scheme;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.builders.NoopValidator.*;

@SuppressWarnings("VisibilityModifier")
public class OAuth2SchemeBuilder {
  //accessible Validator
  String name;
  String flowType;
  String description;
  String authorizationUrl;
  String tokenUrl;
  String refreshUrl;
  final List<AuthorizationScope> scopes = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();
  private Validator<OAuth2SchemeBuilder> validator = new OAuth2SchemeValidator<>();

  public OAuth2SchemeBuilder(String flowType) {
    this.flowType = flowType;
  }

  public OAuth2SchemeBuilder name(String name) {
    this.name = name;
    return this;
  }

  public OAuth2SchemeBuilder description(String description) {
    this.description = description;
    return this;
  }

  public OAuth2SchemeBuilder authorizationUrl(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
    return this;
  }

  public OAuth2SchemeBuilder tokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
    return this;
  }

  public OAuth2SchemeBuilder refreshUrl(String refreshUrl) {
    this.refreshUrl = refreshUrl;
    return this;
  }

  public OAuth2SchemeBuilder scopes(List<AuthorizationScope> scopes) {
    this.scopes.addAll(nullToEmptyList(scopes));
    return this;
  }

  public OAuth2SchemeBuilder extensions(List<VendorExtension> extensions) {
    this.extensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  public OAuth2SchemeBuilder validator(Validator<OAuth2SchemeBuilder> validator) {
    this.validator = validator;
    return this;
  }

  public OAuth2Scheme build() {
    List<ValidationResult> results = validator.validate(this);
    if (logProblems(results).size() > 0) {
      return null;
    }
    return new OAuth2Scheme(
        name,
        flowType,
        description,
        authorizationUrl,
        tokenUrl,
        refreshUrl,
        scopes,
        extensions);
  }
}