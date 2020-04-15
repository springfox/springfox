package springfox.documentation.builders;

import springfox.documentation.service.CollectionFormat;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.SimpleParameterSpecification;
import springfox.documentation.service.ParameterStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleParameterSpecificationBuilder {
  private final RequestParameterBuilder owner;
  private final List<Example> examples = new ArrayList<>();
  private final List<ElementFacet> facets = new ArrayList<>();

  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private Boolean allowEmptyValue;
  private String defaultValue;
  private ModelSpecification model;
  private Example scalarExample;
  private CollectionFormat collectionFormat;


  SimpleParameterSpecificationBuilder(RequestParameterBuilder owner) {
    this.owner = owner;
  }

  public SimpleParameterSpecificationBuilder style(ParameterStyle style) {
    this.style = style;
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

  public SimpleParameterSpecificationBuilder examples(Collection<Example> examples) {
    this.examples.addAll(examples);
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

  public SimpleParameterSpecificationBuilder scalarExample(Example example) {
    this.scalarExample = example;
    return this;
  }

  public SimpleParameterSpecificationBuilder facet(ElementFacet facet) {
    if (facet != null) {
      this.facets.add(facet);
    }
    return this;
  }

  SimpleParameterSpecification create() {
    return new SimpleParameterSpecification(
        style,
        collectionFormat,
        explode,
        allowReserved,
        allowEmptyValue,
        defaultValue,
        model,
        facets,
        scalarExample,
        examples);
  }

  public RequestParameterBuilder build() {
    return owner;
  }
}