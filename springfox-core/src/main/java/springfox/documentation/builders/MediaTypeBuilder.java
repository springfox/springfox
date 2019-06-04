package springfox.documentation.builders;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.VendorExtension;

import java.util.List;

public class MediaTypeBuilder {
  private org.springframework.http.MediaType mediaType;
  private ModelSpecification model;
  private List<Example> examples;
  private List<VendorExtension> vendorExtensions;

  public MediaTypeBuilder withModel(ModelSpecification model) {
    this.model = model;
    return this;
  }

  public MediaTypeBuilder withExamples(List<Example> examples) {
    this.examples = examples;
    return this;
  }

  public MediaTypeBuilder withMediaType(String mediaType) {
    this.mediaType = org.springframework.http.MediaType.parseMediaType(mediaType);
    return this;
  }


  public MediaTypeBuilder withMediaType(org.springframework.http.MediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }


  public MediaTypeBuilder withVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions = vendorExtensions;
    return this;
  }

  public MediaType createMediaType() {
    return new springfox.documentation.service.MediaTypeBuilder()
        .withMediaType(mediaType)
        .withModel(model)
        .withExamples(examples)
        .withVendorExtensions(vendorExtensions)
        .createMediaType();
  }
}