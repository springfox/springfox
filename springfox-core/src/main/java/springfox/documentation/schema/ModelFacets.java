package springfox.documentation.schema;

import springfox.documentation.common.ExternalDocumentation;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ModelFacets implements ElementFacetSource {
  private final String title;
  private final String description;
  private final Boolean nullable;
  private final Boolean deprecated;
  private final Set<ElementFacet> elementFacets = new HashSet<>();
  private final ExternalDocumentation externalDocumentation;
  private final Xml xml;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public ModelFacets(
      String title,
      String description,
      Boolean nullable,
      Boolean deprecated,
      Collection<ElementFacet> facets,
      Xml xml,
      ExternalDocumentation externalDocumentation,
      List<Example> examples,
      List<VendorExtension> extensions) {
    this.title = title;
    this.nullable = nullable;
    this.deprecated = deprecated;
    this.elementFacets.addAll(facets);
    this.xml = xml;
    this.externalDocumentation = externalDocumentation;
    this.description = description;
    this.examples.addAll(examples);
    this.extensions.addAll(extensions);
  }

  public String getTitle() {
    return title;
  }

  public Boolean getNullable() {
    return nullable;
  }

  public Boolean getDeprecated() {
    return deprecated;
  }

  public ExternalDocumentation getExternalDocumentation() {
    return externalDocumentation;
  }

  public List<Example> getExamples() {
    return examples;
  }

  public String getDescription() {
    return description;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }

  public Collection<ElementFacet> getFacets() {
    return elementFacets;
  }

  @Override
  public <T extends ElementFacet> Optional<T> elementFacet(Class<T> clazz) {
    return elementFacets.stream()
                        .filter(e -> e != null && e.getClass().isAssignableFrom(clazz))
                        .map(clazz::cast)
                        .findFirst();
  }

  public Xml getXml() {
    return xml;
  }

  @SuppressWarnings({
      "CyclomaticComplexity",
      "NPathComplexity" })
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelFacets that = (ModelFacets) o;
    return Objects.equals(title, that.title) &&
        Objects.equals(description, that.description) &&
        Objects.equals(nullable, that.nullable) &&
        Objects.equals(deprecated, that.deprecated) &&
        Objects.equals(elementFacets, that.elementFacets) &&
        Objects.equals(externalDocumentation, that.externalDocumentation) &&
        Objects.equals(xml, that.xml) &&
        Objects.equals(examples, that.examples) &&
        Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        title,
        description,
        nullable,
        deprecated,
        elementFacets,
        externalDocumentation,
        xml,
        examples,
        extensions);
  }

  @Override
  public String toString() {
    return "ModelFacets{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", nullable=" + nullable +
        ", deprecated=" + deprecated +
        ", elementFacets=" + elementFacets +
        ", externalDocumentation=" + externalDocumentation +
        ", xml=" + xml +
        ", examples=" + examples +
        ", extensions=" + extensions +
        '}';
  }
}
