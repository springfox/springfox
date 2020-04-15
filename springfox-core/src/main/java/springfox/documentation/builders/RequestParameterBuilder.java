package springfox.documentation.builders;

import springfox.documentation.common.Either;
import springfox.documentation.schema.ContentSpecificationBuilder;
import springfox.documentation.schema.SimpleParameterSpecificationBuilder;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.SimpleParameterSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

public class RequestParameterBuilder {
  private String name;
  private ParameterType in;
  private String description;
  private Boolean required;
  private Boolean deprecated;
  private Boolean allowEmptyValue;
  private Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification;
  private final List<VendorExtension> extensions = new ArrayList<>();

  private SimpleParameterSpecificationBuilder simpleParameterBuilder;
  private ContentSpecificationBuilder contentSpecificationBuilder;
  private String defaultValue;
  private boolean allowMultiple;
  private AllowableListValues allowableValues;
  private int order;

  public RequestParameterBuilder name(String name) {
    this.name = name;
    return this;
  }

  public RequestParameterBuilder in(ParameterType in) {
    this.in = in;
    return this;
  }

  public RequestParameterBuilder description(String description) {
    this.description = description;
    return this;
  }

  public RequestParameterBuilder required(Boolean required) {
    this.required = required;
    return this;
  }

  public RequestParameterBuilder deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public RequestParameterBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }



  public RequestParameterBuilder parameterSpecification(
      ContentSpecification spec) {
    this.parameterSpecification = new Either<>(null, spec);
    return this;
  }

  public RequestParameterBuilder parameterSpecification(
      SimpleParameterSpecification spec) {
    this.parameterSpecification = new Either<>(spec, null);
    return this;
  }
  

  public RequestParameterBuilder extensions(List<VendorExtension> extensions) {
    this.extensions.addAll(extensions);
    return this;
  }

  public RequestParameterBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public RequestParameterBuilder allowMultiple(boolean allowMultiple) {
    this.allowMultiple = allowMultiple;
    return this;
  }

  public RequestParameterBuilder allowableValues(AllowableListValues allowableValues) {
    this.allowableValues = allowableValues;
    return this;
  }

  public RequestParameterBuilder order(int order) {
    this.order = order;
    return this;
  }

  public RequestParameter build() {
    return new RequestParameter(
        name,
        in,
        description,
        required,
        deprecated,
        defaultValue,
        allowMultiple,
        allowEmptyValue,
        allowableValues,
        parameterSpecification,
        order,
        extensions);
  }
}