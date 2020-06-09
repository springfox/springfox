package springfox.documentation.service;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class SimpleParameterSpecification {
  private final ParameterStyle style;
  private final Boolean explode;
  private final Boolean allowReserved;
  private final ModelSpecification model;
  private final List<ElementFacet> facets = new ArrayList<>();
  private final Boolean allowEmptyValue;
  private final String defaultValue;
  private final CollectionFormat collectionFormat;

  @SuppressWarnings("ParameterNumber")
  public SimpleParameterSpecification(
      ParameterStyle style,
      CollectionFormat collectionFormat,
      Boolean explode,
      Boolean allowReserved,
      Boolean allowEmptyValue,
      String defaultValue,
      ModelSpecification model,
      List<ElementFacet> facets) {
    this.style = style;
    this.collectionFormat = collectionFormat;
    this.explode = explode;
    this.allowReserved = allowReserved;
    this.allowEmptyValue = allowEmptyValue;
    this.defaultValue = defaultValue;
    this.model = model;
    this.facets.addAll(facets);
  }

  public ParameterStyle getStyle() {
    return style;
  }

  public Boolean getExplode() {
    return explode;
  }

  public Boolean nullSafeIsExplode() {
    return explode == null ? false : explode;
  }

  public Boolean getAllowReserved() {
    return allowReserved;
  }

  public ModelSpecification getModel() {
    return model;
  }


  public CollectionFormat getCollectionFormat() {
    return collectionFormat;
  }

  public Boolean getAllowEmptyValue() {
    return allowEmptyValue;
  }

  public List<ElementFacet> getFacets() {
    return facets;
  }

  @SuppressWarnings("unchecked")
  public <T extends ElementFacet> Optional<T> facetOfType(Class<T> clazz) {
    return facets.stream()
        .filter(f -> clazz.isAssignableFrom(f.getClass()))
        .map(t -> (T) t)
        .findFirst();
  }

  public String getDefaultValue() {
    return defaultValue;
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
    SimpleParameterSpecification that = (SimpleParameterSpecification) o;
    return style == that.style &&
        Objects.equals(explode, that.explode) &&
        Objects.equals(allowReserved, that.allowReserved) &&
        Objects.equals(allowEmptyValue, that.allowEmptyValue) &&
        Objects.equals(defaultValue, that.defaultValue) &&
        collectionFormat == that.collectionFormat &&
        Objects.equals(model, that.model) &&
        facets.equals(that.facets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        style,
        explode,
        allowReserved,
        allowEmptyValue,
        defaultValue,
        collectionFormat,
        model,
        facets);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SimpleParameterSpecification.class.getSimpleName() + "[", "]")
        .add("style=" + style)
        .add("explode=" + explode)
        .add("allowReserved=" + allowReserved)
        .add("allowEmptyValue=" + allowEmptyValue)
        .add("defaultValue='" + defaultValue + "'")
        .add("collectionFormat=" + collectionFormat)
        .add("model=" + model)
        .add("facets=" + facets)
        .toString();
  }
}
