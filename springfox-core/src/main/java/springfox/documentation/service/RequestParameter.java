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
  public static final int DEFAULT_PRECEDENCE = 0;
  private final String name;
  private final int parameterIndex;
  private final ParameterType in;
  private final String description;
  private final Boolean required;
  private final Boolean deprecated;
  private final Boolean hidden;
  private final ParameterSpecification parameterSpecification;
  private final Integer precedence; //This is an internal property
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
      int precedence,
      List<VendorExtension> extensions,
      int parameterIndex) {

    this.name = name;
    this.in = in;
    this.description = description;
    this.required = required;
    this.deprecated = deprecated;
    this.hidden = hidden;
    this.parameterSpecification = parameterSpecification;
    this.precedence = precedence;
    this.parameterIndex = parameterIndex;
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

  public Integer getPrecedence() {
    return precedence;
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

  public int getParameterIndex() {
    return parameterIndex;
  }

  public Boolean getHidden() {
    return hidden;
  }

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestParameter that = (RequestParameter) o;
    return parameterIndex == that.parameterIndex &&
        Objects.equals(name, that.name) &&
        in == that.in &&
        Objects.equals(description, that.description) &&
        Objects.equals(required, that.required) &&
        Objects.equals(deprecated, that.deprecated) &&
        Objects.equals(hidden, that.hidden) &&
        Objects.equals(parameterSpecification, that.parameterSpecification) &&
        Objects.equals(precedence, that.precedence) &&
        Objects.equals(scalarExample, that.scalarExample) &&
        Objects.equals(examples, that.examples) &&
        Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name,
        parameterIndex,
        in,
        description,
        required,
        deprecated,
        hidden,
        parameterSpecification,
        precedence,
        scalarExample,
        examples,
        extensions);
  }

  @Override
  public String toString() {
    return "RequestParameter{" +
        "name='" + name + '\'' +
        ", parameterIndex=" + parameterIndex +
        ", in=" + in +
        ", description='" + description + '\'' +
        ", required=" + required +
        ", deprecated=" + deprecated +
        ", hidden=" + hidden +
        ", parameterSpecification=" + parameterSpecification +
        ", precedence=" + precedence +
        ", scalarExample=" + scalarExample +
        ", examples=" + examples +
        ", extensions=" + extensions +
        '}';
  }
}
