package springfox.documentation.schema;

import springfox.documentation.service.DocumentationReference;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ModelFacets implements ElementFacetSource {
  private final ModelKey modelKey;
  private final String title;
  private final String description;
  private final Boolean nullable;
  private final Boolean deprecated;
  private final Set<ElementFacet> elementFacets = new HashSet<>();
  private final DocumentationReference externalDocumentation; //TODO: change to external documentation or otherwise
  private final Xml xml;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public ModelFacets(
      ModelKey modelKey,
      String title,
      String description,
      Boolean nullable,
      Boolean deprecated,
      EnumerationFacet enumerationFacet,
      Xml xml,
      DocumentationReference externalDocumentation,
      List<Example> examples,
      List<VendorExtension> extensions) {
    this.modelKey = modelKey;
    this.title = title;
    this.nullable = nullable;
    this.deprecated = deprecated;
    if (enumerationFacet != null) { //TODO: Fix this when its not an individual facet
      this.elementFacets.add(enumerationFacet);
    }
    this.xml = xml;
    this.externalDocumentation = externalDocumentation;
    this.description = description;
    this.examples.addAll(examples);
    this.extensions.addAll(extensions);
  }

  public ModelKey getModelKey() {
    return modelKey;
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

  public DocumentationReference getExternalDocumentation() {
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

  //TODO: Fix this to accept other element facets
  public EnumerationFacet getEnumerationFacet() {
    return elementFacets.stream()
        .filter(e -> e instanceof EnumerationFacet)
        .map(EnumerationFacet.class::cast)
        .findFirst()
        .orElse(null);
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

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelFacets that = (ModelFacets) o;
    return Objects.equals(modelKey, that.modelKey) &&
        Objects.equals(title, that.title) &&
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
        modelKey,
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
        "modelKey=" + modelKey +
        ", title='" + title + '\'' +
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
