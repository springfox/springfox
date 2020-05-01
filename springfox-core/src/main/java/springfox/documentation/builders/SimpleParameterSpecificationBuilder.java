package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static springfox.documentation.builders.ElementFacets.*;

public class SimpleParameterSpecificationBuilder {
  private final RequestParameterBuilder owner;
  private final Map<Class<?>, ElementFacetBuilder> facetBuilders = new HashMap<>();

  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private Boolean allowEmptyValue;
  private String defaultValue;
  private ModelSpecification model;
  private CollectionFormat collectionFormat;

  SimpleParameterSpecificationBuilder(RequestParameterBuilder owner) {
    this.owner = owner;
  }

  public SimpleParameterSpecificationBuilder style(ParameterStyle style) {
    this.style = style;
    this.explode(style == ParameterStyle.FORM);
    return this;
  }

  public SimpleParameterSpecificationBuilder explode(Boolean explode) {
    this.explode = explode;
    return this;
  }

  public SimpleParameterSpecificationBuilder allowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  public SimpleParameterSpecificationBuilder model(ModelSpecification model) {
    this.model = model;
    return this;
  }

  public SimpleParameterSpecificationBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }

  public SimpleParameterSpecificationBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public SimpleParameterSpecificationBuilder collectionFormat(CollectionFormat collectionFormat) {
    this.collectionFormat = collectionFormat;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(clazz, builderFactory(this, clazz));
    return (T) this.facetBuilders.get(clazz);
  }

  SimpleParameterSpecification build() {
    List<ElementFacet> facets = facetBuilders.values().stream()
        .filter(Objects::nonNull)
        .map(ElementFacetBuilder::build)
        .collect(Collectors.toList());

    return new SimpleParameterSpecification(
        style,
        collectionFormat,
        explode,
        allowReserved,
        allowEmptyValue,
        defaultValue,
        model,
        facets
    );
  }

  public RequestParameterBuilder yield() {
    return owner;
  }

  public SimpleParameterSpecificationBuilder copyOf(SimpleParameterSpecification simple) {
    for (ElementFacet each :
        simple.getFacets()) {
      this.facetBuilder(each.facetBuilder())
          .copyOf(each);
    }
    return this.collectionFormat(simple.getCollectionFormat())
        .allowEmptyValue(simple.getAllowEmptyValue())
        .allowReserved(simple.getAllowReserved())
        .defaultValue(simple.getDefaultValue())
        .explode(simple.getExplode())
        .model(simple.getModel())
        .style(simple.getStyle());
  }
}