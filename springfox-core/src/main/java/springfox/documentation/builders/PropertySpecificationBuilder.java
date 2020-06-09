package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.Xml;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.builders.ElementFacets.*;

//TODO: Change builders to not have with
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
  private final CompoundModelSpecificationBuilder parent;

  public PropertySpecificationBuilder(String name) {
    this(
        name,
        null);
  }

  public PropertySpecificationBuilder(
      String name,
      CompoundModelSpecificationBuilder parent) {
    this.name = name;
    this.parent = parent;
  }

  public PropertySpecificationBuilder description(String description) {
    this.description = defaultIfAbsent(emptyToNull(description), this.description);
    return this;
  }

  public PropertySpecificationBuilder type(ModelSpecification type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T extends ElementFacetBuilder> T facetBuilder(Class<T> clazz) {
    this.facetBuilders.computeIfAbsent(
        clazz,
        builderFactory(
            this,
            clazz));
    return (T) this.facetBuilders.get(clazz);
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
    this.readOnly = readOnly;
    return this;
  }

  public PropertySpecificationBuilder writeOnly(Boolean writeOnly) {
    this.writeOnly = writeOnly;
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

  public CompoundModelSpecificationBuilder yield() {
    return parent;
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