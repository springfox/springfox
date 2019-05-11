package springfox.documentation.service;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;

import java.util.ArrayList;
import java.util.List;

public class MediaType {
  private final org.springframework.http.MediaType mediaType;
  private final ModelSpecification model;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  public MediaType(
      org.springframework.http.MediaType mediaType,
      ModelSpecification model,
      List<Example> examples,
      List<VendorExtension> vendorExtensions) {
    this.mediaType = mediaType;
    this.model = model;
    this.examples.addAll(examples);
    this.vendorExtensions.addAll(vendorExtensions);
  }

  public ModelSpecification getModel() {
    return model;
  }

  public List<Example> getExamples() {
    return examples;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public org.springframework.http.MediaType getMediaType() {
    return mediaType;
  }
}
