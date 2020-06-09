package springfox.documentation.builders;

import java.util.ArrayList;
import java.util.List;

public class RequestParameterBuilderValidator implements Validator<RequestParameterBuilder> {
  @Override
  public List<ValidationResult> validate(RequestParameterBuilder builder) {
    List<ValidationResult> results = new ArrayList<>();
    if (builder.name == null) {
      results.add(
          new ValidationResult(
              "RequestParameter",
              "name",
              "Parameter name is required"));
    }
    if (builder.in == null) {
      results.add(new ValidationResult(
          "RequestParameter",
          "in",
          String.format("Parameter %s is required", builder.name)));
    }
    if (builder.simpleParameterBuilder != null && builder.contentSpecificationBuilder != null) {
      results.add(new ValidationResult(
          "RequestParameter",
          "in",
          String.format("Parameter %s can be either a simple parameter or content, but not both", builder.name)));
    }
    return results;
  }
}
