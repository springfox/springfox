package springfox.documentation.service;

import springfox.documentation.common.Either;
import springfox.documentation.schema.ContentSpecification;
import springfox.documentation.schema.SimpleParameterSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @since 3.0.0
 */
public class RequestParameter {
  private final String name;
  private final ParameterType in;
  private final String description;
  private final Boolean required;
  private final Boolean deprecated;
  private final Boolean allowEmptyValue;
  private final Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification;
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public RequestParameter(
      String name,
      ParameterType in,
      String description,
      Boolean required,
      Boolean deprecated,
      Boolean allowEmptyValue,
      Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification,
      List<VendorExtension> extensions) {

    this.name = name;
    this.in = in;
    this.description = description;
    this.required = required;
    this.deprecated = deprecated;
    this.allowEmptyValue = allowEmptyValue;
    this.parameterSpecification = parameterSpecification;
    this.extensions.addAll(extensions);
  }

  public String getName() {
    return name;
  }

  public ParameterType getIn() {
    return in;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getRequired() {
    return required;
  }

  public Boolean getDeprecated() {
    return deprecated;
  }

  public Boolean getAllowEmptyValue() {
    return allowEmptyValue;
  }

  public Either<SimpleParameterSpecification, ContentSpecification> getParameterSpecification() {
    return parameterSpecification;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestParameter that = (RequestParameter) o;
    return Objects.equals(name, that.name) &&
        in == that.in;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, in);
  }
}
