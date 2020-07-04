package springfox.documentation.builders;

import org.springframework.lang.NonNull;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.NumericElementFacetBuilder;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.Xml;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.builders.ElementFacets.*;

public class PropertySpecificationBuilder {

  private final String name;
  private String description;
  private ModelSpecification type;
  private Boolean nullable;
  private Boolean required;
  private Boolean readOnly;
  private Boolean writeOnly;
  private Boolean deprecated;
  private Boolean allowEmptyValue;
  private Boolean isHidden;
  private int position;
  private Object example;
  private Object defaultValue;
  private Xml xml;

  private final Map<Class<?>, ElementFacetBuilder> facetBuilders = new HashMap<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  public PropertySpecificationBuilder(String name) {
    this.name = name;
  }

  public PropertySpecificationBuilder description(String description) {
    this.description = defaultIfAbsent(emptyToNull(description), this.description);
    return this;
  }

  //TODO: fix this #builder
  public PropertySpecificationBuilder type(ModelSpecification type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(clazz, builderFactory(clazz));
    return (T) this.facetBuilders.get(clazz);
  }

  public PropertySpecificationBuilder collectionFacet(
      @NonNull Consumer<CollectionElementFacetBuilder> facet) {
    facet.accept(facetBuilder(CollectionElementFacetBuilder.class));
    return this;
  }

  public PropertySpecificationBuilder stringFacet(
      @NonNull Consumer<StringElementFacetBuilder> facet) {
    facet.accept(facetBuilder(StringElementFacetBuilder.class));
    return this;
  }

  public PropertySpecificationBuilder numericFacet(
      @NonNull Consumer<NumericElementFacetBuilder> facet) {
    facet.accept(facetBuilder(NumericElementFacetBuilder.class));
    return this;
  }

  public PropertySpecificationBuilder enumerationFacet(
      @NonNull Consumer<EnumerationElementFacetBuilder> facet) {
    facet.accept(facetBuilder(EnumerationElementFacetBuilder.class));
    return this;
  }

  public PropertySpecificationBuilder nullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public PropertySpecificationBuilder required(Boolean required) {
    this.required = required;
    return this;
  }

  public PropertySpecificationBuilder readOnly(Boolean readOnly) {
    this.readOnly = defaultIfAbsent(Boolean.TRUE.equals(readOnly) ? true : null, this.readOnly);
    return this;
  }

  public PropertySpecificationBuilder writeOnly(Boolean writeOnly) {
    this.writeOnly = defaultIfAbsent(Boolean.TRUE.equals(writeOnly) ? true : null, this.writeOnly);
    return this;
  }

  public PropertySpecificationBuilder deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public PropertySpecificationBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }

  public PropertySpecificationBuilder isHidden(Boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  public PropertySpecificationBuilder position(int position) {
    this.position = position;
    return this;
  }

  public PropertySpecificationBuilder example(Object example) {
    this.example = example;
    return this;
  }

  public PropertySpecificationBuilder defaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public PropertySpecificationBuilder xml(Xml xml) {
    this.xml = xml;
    return this;
  }

  public PropertySpecificationBuilder vendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public PropertySpecification build() {
    List<ElementFacet> facets = facetBuilders.values()
        .stream()
        .filter(Objects::nonNull)
        .map(ElementFacetBuilder::build)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    if (xml != null && isEmpty(xml.getName())) {
      xml.setName(name);
    }

    return new PropertySpecification(
        name,
        description,
        type,
        facets,
        nullable,
        required,
        readOnly,
        writeOnly,
        deprecated,
        allowEmptyValue,
        isHidden,
        position,
        example,
        defaultValue,
        xml,
        vendorExtensions);
  }
}