package springfox.documentation.builders;

import springfox.documentation.common.ExternalDocumentation;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.NumericElementFacetBuilder;
import springfox.documentation.schema.Xml;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static springfox.documentation.builders.ElementFacets.*;

public class ModelFacetsBuilder {
  private ModelKey modelKey;
  private String title;
  private String description;
  private Boolean nullable;
  private Boolean deprecated;
  private ExternalDocumentation externalDocumentation;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();
  private final Map<Class<?>, ElementFacetBuilder> facetBuilders = new HashMap<>();
  private Xml xml;

  public ModelFacetsBuilder modelKey(ModelKey modelKey) {
    this.modelKey = modelKey;
    return this;
  }

  public ModelFacetsBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ModelFacetsBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ModelFacetsBuilder nullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public ModelFacetsBuilder deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public ModelFacetsBuilder externalDocumentation(ExternalDocumentation externalDocumentation) {
    this.externalDocumentation = externalDocumentation;
    return this;
  }

  public ModelFacetsBuilder examples(List<Example> examples) {
    this.examples.addAll(examples);
    return this;
  }

  public ModelFacetsBuilder extensions(List<VendorExtension> extensions) {
    this.extensions.addAll(extensions);
    return this;
  }

  @SuppressWarnings("unchecked")
  private <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(clazz, builderFactory(clazz));
    return (T) this.facetBuilders.get(clazz);
  }

  public ModelFacetsBuilder collectionFacet(
      Consumer<CollectionElementFacetBuilder> facet) {
    facet.accept(facetBuilder(CollectionElementFacetBuilder.class));
    return this;
  }

  public ModelFacetsBuilder stringFacet(
      Consumer<StringElementFacetBuilder> facet) {
    facet.accept(facetBuilder(StringElementFacetBuilder.class));
    return this;
  }

  public ModelFacetsBuilder numericFacet(
      Consumer<NumericElementFacetBuilder> facet) {
    facet.accept(facetBuilder(NumericElementFacetBuilder.class));
    return this;
  }

  public ModelFacetsBuilder enumerationFacet(
      Consumer<EnumerationElementFacetBuilder> facet) {
    facet.accept(facetBuilder(EnumerationElementFacetBuilder.class));
    return this;
  }

  public ModelFacetsBuilder xml(Xml xml) {
    this.xml = xml;
    return this;
  }

  public ModelFacets build() {
    List<ElementFacet> facets = facetBuilders.values().stream()
                                             .filter(Objects::nonNull)
                                             .map(ElementFacetBuilder::build)
                                             .filter(Objects::nonNull)
                                             .collect(Collectors.toList());
    return new ModelFacets(
        modelKey,
        title,
        description,
        nullable,
        deprecated,
        facets,
        xml,
        externalDocumentation,
        examples,
        extensions);
  }

  public ModelFacetsBuilder copyOf(ModelFacets other) {
    for (ElementFacet each : other.getFacets()) {
      this.facetBuilder(each.facetBuilder())
          .copyOf(each);
    }
    return this.modelKey(other.getModelKey())
               .title(other.getTitle())
               .description(other.getDescription())
               .nullable(other.getNullable())
               .deprecated(other.getDeprecated())
               .extensions(other.getExtensions())
               .externalDocumentation(other.getExternalDocumentation())
               .examples(other.getExamples())
               .xml(other.getXml());
  }
}