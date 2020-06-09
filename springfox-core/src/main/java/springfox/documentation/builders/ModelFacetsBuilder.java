package springfox.documentation.builders;

import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.Xml;
import springfox.documentation.service.DocumentationReference;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

public class ModelFacetsBuilder {
  private final ModelSpecificationBuilder parent;
  private ModelKey modelKey;
  private String title;
  private String description;
  private Boolean nullable;
  private Boolean deprecated;
  private DocumentationReference externalDocumentation;
  private EnumerationFacet enumerationFacet;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> extensions = new ArrayList<>();
  private Xml xml;

  public ModelFacetsBuilder(ModelSpecificationBuilder parent) {
    this.parent = parent;
  }

  public ModelFacetsBuilder withModelKey(ModelKey modelKey) {
    this.modelKey = modelKey;
    return this;
  }

  public ModelFacetsBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public ModelFacetsBuilder description(String description) {
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
    this.examples.addAll(examples);
    return this;
  }

  public ModelFacetsBuilder withExtensions(List<VendorExtension> extensions) {
    this.extensions.addAll(extensions);
    return this;
  }

  public ModelFacetsBuilder enumeration(EnumerationFacet enumerationFacet) {
    this.enumerationFacet = enumerationFacet;
    return this;
  }

  public ModelFacetsBuilder xml(Xml xml) {
    this.xml = xml;
    return this;
  }

  public ModelSpecificationBuilder yield() {
    return parent;
  }

  public ModelFacets build() {
    return new ModelFacets(
        modelKey,
        title,
        description,
        nullable,
        deprecated,
        enumerationFacet, //TODO: make this a set of facets
        xml,
        externalDocumentation,
        examples,
        extensions);
  }

  public ModelFacetsBuilder copyOf(ModelFacets other) {
    return this.withModelKey(other.getModelKey())
        .withTitle(other.getTitle())
        .description(other.getDescription())
        .withNullable(other.getNullable())
        .withDeprecated(other.getDeprecated())
        .enumeration(other.getEnumerationFacet())
        .withExtensions(other.getExtensions())
        .withExternalDocumentation(other.getExternalDocumentation())
        .withExamples(other.getExamples())
        .xml(other.getXml());
  }
}