package springfox.documentation.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.common.Either;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SimpleParameterSpecification;
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
  private Boolean hidden = false;
  private final List<VendorExtension> extensions = new ArrayList<>();
  private SimpleParameterSpecificationBuilder simpleParameterBuilder;
  private ContentSpecificationBuilder contentSpecificationBuilder;
  private int order;
  private Validator<RequestParameterBuilder> validator = new NoopValidator<>();

  public RequestParameterBuilder name(String name) {
    this.name = name;
    return this;
  }

  public RequestParameterBuilder in(ParameterType in) {
    this.in = in;
    return this;
  }

  public RequestParameterBuilder in(String in) {
    this.in = ParameterType.from(in);
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

  public RequestParameterBuilder validator(RequestParameterBuilderValidator validator) {
    this.validator = validator;
    return this;
  }

  public RequestParameter build() {
    List<ValidationResult> results = validator.validate(this);
    if (logProblems(results).size() > 0) {
      return null;
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

  public RequestParameterBuilder from(RequestParameter source) {
    source.getParameterSpecification()
        .getLeft()
        .map(simple -> {
          for (ElementFacet each :
              simple.getFacets()) {
            this.simpleParameterBuilder()
                .facetBuilder(each.facetBuilder())
                .copyOf(each);
          }
          this.simpleParameterBuilder()
              .collectionFormat(simple.getCollectionFormat())
              .allowEmptyValue(simple.getAllowEmptyValue())
              .allowReserved(simple.getAllowReserved())
              .defaultValue(simple.getDefaultValue())
              .examples(simple.getExamples())
              .explode(simple.getExplode())
              .scalarExample(simple.getScalarExample())
              .model(simple.getModel())
              .style(simple.getStyle());
          return simple;
        });
    source.getParameterSpecification()
        .getRight()
        .map(content -> this.contentSpecificationBuilder()
            .mediaTypes(content.getMediaTypes()));
    return this.in(source.getIn())
        .required(source.getRequired())
        .hidden(source.getHidden())
        .deprecated(source.getDeprecated())
        .extensions(source.getExtensions())
        .name(source.getName())
        .description(source.getDescription())
        .order(source.getOrder());
  }

}