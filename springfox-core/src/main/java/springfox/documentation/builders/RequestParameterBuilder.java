package springfox.documentation.builders;

import springfox.documentation.common.Either;
import springfox.documentation.schema.ContentSpecification;
import springfox.documentation.schema.ContentSpecificationBuilder;
import springfox.documentation.schema.SimpleParameterSpecification;
import springfox.documentation.schema.SimpleParameterSpecificationBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.VendorExtension;

import java.util.List;

public class RequestParameterBuilder {
  private String name;
  private ParameterType in;
  private String description;
  private Boolean required;
  private Boolean deprecated;
  private Boolean allowEmptyValue;
  private Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification;
  private List<VendorExtension> extensions;

  private SimpleParameterSpecificationBuilder simpleParameterBuilder;
  private ContentSpecificationBuilder contentSpecificationBuilder;

  public RequestParameterBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public RequestParameterBuilder withIn(ParameterType in) {
    this.in = in;
    return this;
  }

  public RequestParameterBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public RequestParameterBuilder withRequired(Boolean required) {
    this.required = required;
    return this;
  }

  public RequestParameterBuilder withDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public RequestParameterBuilder withAllowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }



  public RequestParameterBuilder withParameterSpecification(
      ContentSpecification spec) {
    this.parameterSpecification = new Either<>(null, spec);
    return this;
  }

  public RequestParameterBuilder withParameterSpecification(
      SimpleParameterSpecification spec) {
    this.parameterSpecification = new Either<>(spec, null);
    return this;
  }
  

  public RequestParameterBuilder withExtensions(List<VendorExtension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public RequestParameter build() {
    return new RequestParameter(
        name,
        in,
        description,
        required,
        deprecated,
        allowEmptyValue,
        parameterSpecification,
        extensions);
  }
}