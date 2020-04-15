package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.Xml;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

public class PropertySpecificationBuilder {
  private String name;
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

  private final List<ElementFacet> facets = new ArrayList<>();
  private final List<VendorExtension<?>> vendorExtensions = new ArrayList<>();

  public PropertySpecificationBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public PropertySpecificationBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public PropertySpecificationBuilder withType(ModelSpecification type) {
    this.type = type;
    return this;
  }

  public PropertySpecificationBuilder withFacets(List<ElementFacet> facets) {
    this.facets.addAll(facets);
    return this;
  }

  public PropertySpecificationBuilder facet(ElementFacet facet) {
    if (facet != null) {
      this.facets.add(facet);
    }
    return this;
  }

  public PropertySpecificationBuilder withNullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public PropertySpecificationBuilder withRequired(Boolean required) {
    this.required = required;
    return this;
  }

  public PropertySpecificationBuilder withReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public PropertySpecificationBuilder withWriteOnly(Boolean writeOnly) {
    this.writeOnly = writeOnly;
    return this;
  }

  public PropertySpecificationBuilder withDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public PropertySpecificationBuilder withAllowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }

  public PropertySpecificationBuilder withIsHidden(Boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  public PropertySpecificationBuilder withPosition(int position) {
    this.position = position;
    return this;
  }

  public PropertySpecificationBuilder withExample(Object example) {
    this.example = example;
    return this;
  }

  public PropertySpecificationBuilder withDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public PropertySpecificationBuilder withXml(Xml xml) {
    this.xml = xml;
    return this;
  }

  public PropertySpecificationBuilder withVendorExtensions(List<VendorExtension<?>> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public PropertySpecification build() {
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