package springfox.documentation.service;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MediaType mediaType1 = (MediaType) o;
    return mediaType.equals(mediaType1.mediaType) &&
        model.equals(mediaType1.model) &&
        examples.equals(mediaType1.examples) &&
        vendorExtensions.equals(mediaType1.vendorExtensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mediaType, model, examples, vendorExtensions);
  }
}
