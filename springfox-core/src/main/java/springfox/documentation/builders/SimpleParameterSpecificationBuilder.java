package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.NumericElementFacetBuilder;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static springfox.documentation.builders.BuilderDefaults.*;
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
//    this.explode(style == ParameterStyle.FORM); //TODO: Is this needed
    return this;
  }

  public SimpleParameterSpecificationBuilder explode(Boolean explode) {
    this.explode = defaultIfAbsent(Boolean.TRUE.equals(explode) ? true : null, this.explode);
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
    this.allowEmptyValue = defaultIfAbsent(Boolean.TRUE.equals(allowEmptyValue) ? true : null, this.allowEmptyValue);
    return this;
  }

  public SimpleParameterSpecificationBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  //TODO: May not be needed for 3.0
  public SimpleParameterSpecificationBuilder collectionFormat(CollectionFormat collectionFormat) {
    this.collectionFormat = collectionFormat;
    return this;
  }

  @SuppressWarnings("unchecked")
  private <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(clazz, builderFactory(clazz));
    return (T) this.facetBuilders.get(clazz);
  }

  public SimpleParameterSpecificationBuilder collectionFacet(
      Consumer<CollectionElementFacetBuilder> facet) {
    facet.accept(facetBuilder(CollectionElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder stringFacet(
      Consumer<StringElementFacetBuilder> facet) {
    facet.accept(facetBuilder(StringElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder numericFacet(
      Consumer<NumericElementFacetBuilder> facet) {
    facet.accept(facetBuilder(NumericElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder enumerationFacet(
      Consumer<EnumerationElementFacetBuilder> facet) {
    facet.accept(facetBuilder(EnumerationElementFacetBuilder.class));
    return this;
  }

  SimpleParameterSpecification build() {
    List<ElementFacet> facets = facetBuilders.values().stream()
        .filter(Objects::nonNull)
        .map(ElementFacetBuilder::build)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    if (explode != null
        && explode
        && model.getCollection().isPresent()) {
      model = model.getCollection().get().getModel();
    }

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