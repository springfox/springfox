package springfox.documentation.service;

import springfox.documentation.common.Either;

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
  private final String defaultValue;
  private final Boolean allowMultiple;
  private final Boolean allowEmptyValue;
  private final AllowableListValues allowableValues;
  private final Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification;
  private final Integer order;
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public RequestParameter(
      String name,
      ParameterType in,
      String description,
      Boolean required,
      Boolean deprecated,
      String defaultValue,
      Boolean allowMultiple,
      Boolean allowEmptyValue,
      AllowableListValues allowableValues,
      Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification,
      int order,
      List<VendorExtension> extensions) {

    this.name = name;
    this.in = in;
    this.description = description;
    this.required = required;
    this.deprecated = deprecated;
    this.defaultValue = defaultValue;
    this.allowMultiple = allowMultiple;
    this.allowEmptyValue = allowEmptyValue;
    this.allowableValues = allowableValues;
    this.parameterSpecification = parameterSpecification;
    this.order = order;
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

  public String getDefaultValue() {
    return defaultValue;
  }

  public Boolean getAllowMultiple() {
    return allowMultiple;
  }

  public AllowableListValues getAllowableValues() {
    return allowableValues;
  }

  public Integer getOrder() {
    return order;
  }

  String getParamType() {
    return in.getIn();
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
