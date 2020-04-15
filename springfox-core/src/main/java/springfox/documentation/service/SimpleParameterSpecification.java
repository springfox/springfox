package springfox.documentation.service;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SimpleParameterSpecification {
  private final ParameterStyle style;
  private final Boolean explode;
  private final Boolean allowReserved;
  private final Boolean allowEmptyValue;
  private final String defaultValue;
  private final CollectionFormat collectionFormat;
  private final ModelSpecification model;
  private final Example scalarExample;
  private final List<Example> examples;
  private final List<ElementFacet> facets = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public SimpleParameterSpecification(
      ParameterStyle style,
      CollectionFormat collectionFormat,
      Boolean explode,
      Boolean allowReserved,
      Boolean allowEmptyValue,
      String defaultValue,
      ModelSpecification model,
      List<ElementFacet> facets,
      Example scalarExample,
      List<Example> examples) {
    this.style = style;
    this.collectionFormat = collectionFormat;
    this.explode = explode;
    this.allowReserved = allowReserved;
    this.allowEmptyValue = allowEmptyValue;
    this.defaultValue = defaultValue;
    this.model = model;
    this.facets.addAll(facets);
    this.scalarExample = scalarExample;
    this.examples = examples;
  }

  public ParameterStyle getStyle() {
    return style;
  }

  public Boolean getExplode() {
    return explode;
  }

  public Boolean getAllowReserved() {
    return allowReserved;
  }

  public ModelSpecification getModel() {
    return model;
  }

  public List<Example> getExamples() {
    return examples;
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

  public String getDefaultValue() {
    return defaultValue;
  }

  public Example getScalarExample() {
    return scalarExample;
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
        Objects.equals(scalarExample, that.scalarExample) &&
        examples.equals(that.examples) &&
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
        scalarExample,
        examples,
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
        .add("scalarExample=" + scalarExample)
        .add("examples=" + examples)
        .add("facets=" + facets)
        .toString();
  }
}
