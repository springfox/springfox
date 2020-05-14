package springfox.documentation.schema;

import springfox.documentation.service.DocumentationReference;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelFacets {
  private final ModelKey modelKey;
  private final String title;
  private final String description;
  private final Boolean nullable;
  private final Boolean deprecated;
  private final EnumerationFacet enumerationFacet;
  private final DocumentationReference externalDocumentation; //TODO: change to external documentation or otherwise
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
      DocumentationReference externalDocumentation,
      List<Example> examples,
      List<VendorExtension> extensions) {
    this.modelKey = modelKey;
    this.title = title;
    this.nullable = nullable;
    this.deprecated = deprecated;
    this.enumerationFacet = enumerationFacet;
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

  public EnumerationFacet getEnumerationFacet() {
    return enumerationFacet;
  }

  @SuppressWarnings("CyclomaticComplexity")
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
        Objects.equals(enumerationFacet, that.enumerationFacet) &&
        Objects.equals(externalDocumentation, that.externalDocumentation) &&
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
        enumerationFacet,
        externalDocumentation,
        examples,
        extensions);
  }
}
