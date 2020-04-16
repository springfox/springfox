package springfox.documentation.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.common.Either;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.SimpleParameterSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

public class RequestParameterBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestParameterBuilder.class);
  private String name;
  private ParameterType in;
  private String description;
  private Boolean required = false;
  private Boolean deprecated;
  private Boolean hidden;
  private final List<VendorExtension> extensions = new ArrayList<>();
  private SimpleParameterSpecificationBuilder simpleParameterBuilder;
  private ContentSpecificationBuilder contentSpecificationBuilder;
  private int order;

  public RequestParameterBuilder name(String name) {
    this.name = name;
    return this;
  }

  public RequestParameterBuilder in(ParameterType in) {
    this.in = in;
    return this;
  }

  public RequestParameterBuilder in(String in) {
    try {
      this.in = ParameterType.valueOf(in);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Unrecognized parameter type {}", in);
    }
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

  public SimpleParameterSpecificationBuilder simpleParameterBuilder() {
    if (simpleParameterBuilder == null) {
      simpleParameterBuilder = new SimpleParameterSpecificationBuilder(this);
    }
    return simpleParameterBuilder;
  }

  public ContentSpecificationBuilder contentSpecificationBuilder() {
    if (contentSpecificationBuilder == null) {
      contentSpecificationBuilder = new ContentSpecificationBuilder(this);
    }
    return contentSpecificationBuilder;
  }

  public RequestParameterBuilder extensions(List<VendorExtension> extensions) {
    this.extensions.addAll(extensions);
    return this;
  }

  public RequestParameterBuilder hidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  public RequestParameterBuilder order(int order) {
    this.order = order;
    return this;
  }

  public RequestParameter build() {
    if (simpleParameterBuilder != null && contentSpecificationBuilder != null) {
      throw new IllegalStateException("Parameter can be either a simple parameter or content, but not both");
    }
    Either<SimpleParameterSpecification, ContentSpecification> parameterSpecification;
    if (simpleParameterBuilder == null && contentSpecificationBuilder != null) {
      parameterSpecification = new Either<>(null, contentSpecificationBuilder.create());
    } else if (simpleParameterBuilder != null) {
      parameterSpecification = new Either<>(simpleParameterBuilder.create(), null);
    } else {
      LOGGER.warn("Parameter has not been initialized to be either simple nor a content parameter");
      parameterSpecification = null;
    }
    return new RequestParameter(
        name,
        in,
        description,
        required,
        deprecated,
        hidden,
        parameterSpecification,
        order,
        extensions);
  }

}