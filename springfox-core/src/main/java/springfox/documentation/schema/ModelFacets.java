package springfox.documentation.schema;

import springfox.documentation.service.DocumentationReference;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

public class ModelFacets {
  private final ModelKey modelKey;
  private final String title;
  private final String description;
  private final Boolean nullable;
  private final Boolean deprecated;
  private final DocumentationReference externalDocumentation;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();

  @SuppressWarnings("ParameterNumber")
  public ModelFacets(
      ModelKey modelKey,
      String title,
      String description,
      Boolean nullable,
      Boolean deprecated,
      DocumentationReference externalDocumentation,
      List<Example> examples,
      List<VendorExtension> extensions) {
    this.modelKey = modelKey;
    this.title = title;
    this.nullable = nullable;
    this.deprecated = deprecated;
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
}
