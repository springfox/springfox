package springfox.documentation.builders;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OAuth2SchemeValidator<T> implements Validator<OAuth2SchemeBuilder> {
  @Override
  public List<ValidationResult> validate(OAuth2SchemeBuilder builder) {
    List<ValidationResult> results = new ArrayList<>();
    if (builder.name == null) {
      results.add(
          new ValidationResult(
              "OAuth2Scheme",
              "name",
              "Parameter name is required"));
    }
    if (builder.flowType == null) {
      results.add(new ValidationResult(
          "OAuth2Scheme",
          "flowType",
          "Flow type is required"));
    }
    switch (builder.flowType) {
      case "implicit":
        requiredAttribute(results, "authorizationUrl", builder.authorizationUrl);
        break;
      case "password":
      case "clientCredentials":
        requiredAttribute(results, "tokenUrl", builder.tokenUrl);
        break;
      case "authorizationCode":
        requiredAttribute(results, "authorizationUrl", builder.authorizationUrl);
        requiredAttribute(results, "tokenUrl", builder.tokenUrl);
        break;
      default:
        results.add(new ValidationResult(
            "OAuth2Scheme",
            "flowType",
            "Flow type should be one of (implicit, password, clientCredentials, authorizationCode)"));
    }
    if (builder.scopes.isEmpty()) {
      results.add(new ValidationResult(
          "OAuth2Scheme",
          "scopes",
          "Scopes are required"));
    }
    return results;
  }

  private void requiredAttribute(
      List<ValidationResult> results,
      String name,
      String value) {
    if (StringUtils.isEmpty(value)) {
      results.add(new ValidationResult(
          "OAuth2Scheme",
          "name",
          String.format("Parameter %s is required", name)));
    }
  }
}
