package springfox.documentation.schema;

import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static springfox.documentation.builders.BuilderDefaults.*;

public class PropertySpecification {
  private final String name;
  private final String description;
  private final ModelSpecification type;

  private final List<ElementFacet> facets = new ArrayList<>();

  private final Boolean nullable;
  private final Boolean required;
  private final Boolean readOnly;
  private final Boolean writeOnly;
  private final Boolean deprecated;
  private final Boolean allowEmptyValue;
  private final Boolean isHidden;
  private final int position;
  private final Object example;
  private final Object defaultValue;

  private final Xml xml;
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public PropertySpecification(
      String name,
      String description,
      ModelSpecification type,
      List<ElementFacet> facets,
      Boolean nullable,
      Boolean required,
      Boolean readOnly,
      Boolean writeOnly,
      Boolean deprecated,
      Boolean allowEmptyValue,
      Boolean isHidden,
      int position,
      Object example,
      Object defaultValue,
      Xml xml,
      List<VendorExtension> vendorExtensions) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.facets.addAll(nullToEmptyList(facets));
    this.nullable = nullable;
    this.required = required;
    this.readOnly = readOnly;
    this.writeOnly = writeOnly;
    this.deprecated = deprecated;
    this.allowEmptyValue = allowEmptyValue;
    this.isHidden = isHidden;
    this.position = position;
    this.example = example;
    this.defaultValue = defaultValue;
    this.xml = xml;
    this.vendorExtensions.addAll(nullToEmptyList(vendorExtensions));
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ModelSpecification getType() {
    return type;
  }

  public List<ElementFacet> getFacets() {
    return facets;
  }

  public <T extends ElementFacet> Optional<T> facetOfType(Class<T> type) {
    return facets.stream()
                 .filter(f -> f.getClass().isAssignableFrom(type))
                 .findFirst()
                 .map(type::cast);
  }

  public Boolean getNullable() {
    return nullable;
  }

  public Boolean getRequired() {
    return required;
  }

  public Boolean getReadOnly() {
    return readOnly;
  }

  public Boolean getWriteOnly() {
    return writeOnly;
  }

  public Boolean getDeprecated() {
    return deprecated;
  }

  public Boolean getAllowEmptyValue() {
    return allowEmptyValue;
  }

  public Boolean getHidden() {
    return isHidden;
  }

  public int getPosition() {
    return position;
  }

  public Object getExample() {
    return example;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public Xml getXml() {
    return xml;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }
}
