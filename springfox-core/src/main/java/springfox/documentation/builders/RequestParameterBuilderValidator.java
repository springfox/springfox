package springfox.documentation.builders;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RequestParameterBuilderValidator implements Validator<RequestParameterBuilder> {
  @Override
  public List<ValidationResult> validate(RequestParameterBuilder builder) {
    List<ValidationResult> results = new ArrayList<>();
    if (StringUtils.isEmpty(builder.name)) {
      results.add(
          new ValidationResult(
              "RequestParameter",
              "name",
              "Parameter name is required"));
    }
    if (StringUtils.isEmpty(builder.in)) {
      results.add(new ValidationResult(
          "RequestParameter",
          "in",
          "Parameter in is required"));
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
