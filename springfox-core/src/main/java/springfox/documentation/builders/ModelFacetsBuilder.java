package springfox.documentation.builders;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.service.DocumentationReference;
import springfox.documentation.service.VendorExtension;

import java.util.List;

public class ModelFacetsBuilder {
  private ModelKey modelKey;
  private String title;
  private String description;
  private Boolean nullable;
  private Boolean deprecated;
  private DocumentationReference externalDocumentation;
  private List<Example> examples;
  private List<VendorExtension> extensions;

  public ModelFacetsBuilder withModelKey(ModelKey modelKey) {
    this.modelKey = modelKey;
    return this;
  }

  public ModelFacetsBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public ModelFacetsBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ModelFacetsBuilder withNullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public ModelFacetsBuilder withDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public ModelFacetsBuilder withExternalDocumentation(DocumentationReference externalDocumentation) {
    this.externalDocumentation = externalDocumentation;
    return this;
  }

  public ModelFacetsBuilder withExamples(List<Example> examples) {
    this.examples = examples;
    return this;
  }

  public ModelFacetsBuilder withExtensions(List<VendorExtension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public ModelFacets builder() {
    return new ModelFacets(
        modelKey,
        title,
        description,
        nullable,
        deprecated,
        externalDocumentation,
        examples,
        extensions);
  }
}