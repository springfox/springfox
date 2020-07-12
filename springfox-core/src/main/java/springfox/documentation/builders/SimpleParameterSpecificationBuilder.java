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

  /**
   * Parameter style
   * @param style - parameter style
   * @return this
   */
  public SimpleParameterSpecificationBuilder style(ParameterStyle style) {
    this.style = style;
    return this;
  }

  /**
   * Use this if we want to explode collections
   * @param explode - explodes collection
   * @return this
   */
  public SimpleParameterSpecificationBuilder explode(Boolean explode) {
    this.explode = defaultIfAbsent(Boolean.TRUE.equals(explode) ? true : null, this.explode);
    return this;
  }

  /**
   * Allows reserved characters
   * @param allowReserved - allow reserved characters
   * @return this
   */
  public SimpleParameterSpecificationBuilder allowReserved(Boolean allowReserved) {
    this.allowReserved = defaultIfAbsent(allowReserved, this.allowReserved);
    return this;
  }

  /**
   * Provides consumer to help build models
   * @param consumer - consumer that facilitates building a model
   * @return this
   */
  public SimpleParameterSpecificationBuilder model(@NonNull Consumer<ModelSpecificationBuilder> consumer) {
    consumer.accept(model);
    return this;
  }

  /**
   * Allows empty values
   * @param allowEmptyValue - flag to indicate empty values are allowed
   * @return this
   */
  public SimpleParameterSpecificationBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = defaultIfAbsent(Boolean.TRUE.equals(allowEmptyValue) ? true : null, this.allowEmptyValue);
    return this;
  }

  /**
   * Default value
   * @param defaultValue - default value
   * @return this
   */
  public SimpleParameterSpecificationBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultIfAbsent(emptyToNull(defaultValue), this.defaultValue);
    return this;
  }

  /**
   * This is only there to set the collection format for swagger 2
   * @param collectionFormat - collection format
   * @return this
   */
  @Deprecated
  public SimpleParameterSpecificationBuilder collectionFormat(CollectionFormat collectionFormat) {
    this.collectionFormat = collectionFormat;
    return this;
  }

  @SuppressWarnings("unchecked")
  private <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(clazz, builderFactory(clazz));
    return (T) this.facetBuilders.get(clazz);
  }

  /**
   * Provides consumer to help uild collection element facet
   * @param facet - consumer that facilitates building a collection facet
   * @return this
   */
  public SimpleParameterSpecificationBuilder collectionFacet(
      @NonNull Consumer<CollectionElementFacetBuilder> facet) {
    facet.accept(facetBuilder(CollectionElementFacetBuilder.class));
    return this;
  }

  /**
   * Provides consumer to help build string element facet
   * @param facet - consumer that facilitates building a string facet
   * @return this
   */
  public SimpleParameterSpecificationBuilder stringFacet(
      @NonNull Consumer<StringElementFacetBuilder> facet) {
    facet.accept(facetBuilder(StringElementFacetBuilder.class));
    return this;
  }

  /**
   * Provides consumer to help build numeric element facet
   * @param facet - consumer that facilitates building a numeric facet
   * @return this
   */
  public SimpleParameterSpecificationBuilder numericFacet(
      @NonNull Consumer<NumericElementFacetBuilder> facet) {
    facet.accept(facetBuilder(NumericElementFacetBuilder.class));
    return this;
  }

  /**
   * Provides consumer to help build enumeration element facet
   * @param facet - consumer that facilitates building a enumeration facet
   * @return this
   */
  public SimpleParameterSpecificationBuilder enumerationFacet(
      @NonNull Consumer<EnumerationElementFacetBuilder> facet) {
    facet.accept(facetBuilder(EnumerationElementFacetBuilder.class));
    return this;
  }

  /**
   * Method they copy from a specification
   * @param other - other spec to copy from
   * @return this
   */
  public SimpleParameterSpecificationBuilder copyOf(SimpleParameterSpecification other) {
    for (ElementFacet each : other.getFacets()) {
      this.facetBuilder(each.facetBuilder())
          .copyOf(each);
    }
    return this.collectionFormat(other.getCollectionFormat())
        .allowEmptyValue(other.getAllowEmptyValue())
        .allowReserved(other.getAllowReserved())
        .defaultValue(other.getDefaultValue())
        .explode(other.getExplode())
        .model(m -> m.copyOf(other.getModel()))
        .style(other.getStyle());
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
}