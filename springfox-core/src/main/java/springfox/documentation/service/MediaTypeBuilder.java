package springfox.documentation.service;

import org.springframework.http.MediaType;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;

import java.util.List;

public class MediaTypeBuilder {
  private MediaType mediaType;
  private ModelSpecification model;
  private List<Example> examples;
  private List<VendorExtension> vendorExtensions;

  public MediaTypeBuilder withMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public MediaTypeBuilder withModel(ModelSpecification model) {
    this.model = model;
    return this;
  }

  public MediaTypeBuilder withExamples(List<Example> examples) {
    this.examples = examples;
    return this;
  }

  public MediaTypeBuilder withVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions = vendorExtensions;
    return this;
  }

  public springfox.documentation.service.MediaType createMediaType() {
    return new springfox.documentation.service.MediaType(
        mediaType,
        model,
        examples,
        vendorExtensions);
  }
}