package springfox.documentation.service;

import springfox.documentation.schema.Example;

import java.util.ArrayList;
import java.util.Collection;
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
  private final Boolean hidden;
  private final ParameterSpecification parameterSpecification;
  private final Integer order;
  private final Example scalarExample;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public RequestParameter(
      String name,
      ParameterType in,
      String description,
      Boolean required,
      Boolean deprecated,
      Boolean hidden,
      ParameterSpecification parameterSpecification,
      Example scalarExample,
      Collection<Example> examples,
      int order,
      List<VendorExtension> extensions) {

    this.name = name;
    this.in = in;
    this.description = description;
    this.required = required;
    this.deprecated = deprecated;
    this.hidden = hidden;
    this.parameterSpecification = parameterSpecification;
    this.order = order;
    this.extensions.addAll(extensions);
    this.scalarExample = scalarExample;
    this.examples.addAll(examples);
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

  public ParameterSpecification getParameterSpecification() {
    return parameterSpecification;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }

  public Integer getOrder() {
    return order;
  }

  String getParamType() {
    return in.getIn();
  }

  public List<Example> getExamples() {
    return examples;
  }

  public Example getScalarExample() {
    return scalarExample;
  }

  public boolean isRequestBody() {
    return false;
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

  public Boolean getHidden() {
    return hidden;
  }
}
