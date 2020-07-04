package springfox.documentation.builders;

import org.springframework.lang.NonNull;
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
  private final Map<Class<?>, ElementFacetBuilder> facetBuilders = new HashMap<>();

  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private Boolean allowEmptyValue;
  private String defaultValue;
  private CollectionFormat collectionFormat;
  private final ModelSpecificationBuilder model = new ModelSpecificationBuilder();

  public SimpleParameterSpecificationBuilder style(ParameterStyle style) {
    this.style = style;
    return this;
  }

  public SimpleParameterSpecificationBuilder explode(Boolean explode) {
    this.explode = defaultIfAbsent(Boolean.TRUE.equals(explode) ? true : null, this.explode);
    return this;
  }

  public SimpleParameterSpecificationBuilder allowReserved(Boolean allowReserved) {
    this.allowReserved = defaultIfAbsent(allowReserved, this.allowReserved);
    return this;
  }

  public SimpleParameterSpecificationBuilder model(@NonNull Consumer<ModelSpecificationBuilder> consumer) {
    consumer.accept(model);
    return this;
  }

  public SimpleParameterSpecificationBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = defaultIfAbsent(Boolean.TRUE.equals(allowEmptyValue) ? true : null, this.allowEmptyValue);
    return this;
  }

  public SimpleParameterSpecificationBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultIfAbsent(emptyToNull(defaultValue), this.defaultValue);
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
      @NonNull Consumer<CollectionElementFacetBuilder> facet) {
    facet.accept(facetBuilder(CollectionElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder stringFacet(
      @NonNull Consumer<StringElementFacetBuilder> facet) {
    facet.accept(facetBuilder(StringElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder numericFacet(
      @NonNull Consumer<NumericElementFacetBuilder> facet) {
    facet.accept(facetBuilder(NumericElementFacetBuilder.class));
    return this;
  }

  public SimpleParameterSpecificationBuilder enumerationFacet(
      @NonNull Consumer<EnumerationElementFacetBuilder> facet) {
    facet.accept(facetBuilder(EnumerationElementFacetBuilder.class));
    return this;
  }

  SimpleParameterSpecification build() {
    ModelSpecification builtModel = model.build();
    if (builtModel == null) {
      return null;
    }
    List<ElementFacet> facets = facetBuilders.values().stream()
        .filter(Objects::nonNull)
        .map(ElementFacetBuilder::build)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (explode != null
        && explode
        && builtModel.getCollection().isPresent()) {
      builtModel = builtModel.getCollection().get().getModel();
    }
    return new SimpleParameterSpecification(
        style,
        collectionFormat,
        explode,
        allowReserved,
        allowEmptyValue,
        defaultValue,
        builtModel,
        facets
    );
  }

  public SimpleParameterSpecificationBuilder copyOf(SimpleParameterSpecification simple) {
    for (ElementFacet each : simple.getFacets()) {
      this.facetBuilder(each.facetBuilder())
          .copyOf(each);
    }
    return this.collectionFormat(simple.getCollectionFormat())
        .allowEmptyValue(simple.getAllowEmptyValue())
        .allowReserved(simple.getAllowReserved())
        .defaultValue(simple.getDefaultValue())
        .explode(simple.getExplode())
        .model(m -> m.copyOf(simple.getModel()))
        .style(simple.getStyle());
  }
}