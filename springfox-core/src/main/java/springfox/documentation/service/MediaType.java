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
  private final List<Encoding> encodings = new ArrayList<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  public MediaType(
      org.springframework.http.MediaType mediaType,
      ModelSpecification model,
      List<Example> examples,
      List<Encoding> encodings,
      List<VendorExtension> vendorExtensions) {
    this.mediaType = mediaType;
    this.model = model;
    this.examples.addAll(examples);
    this.encodings.addAll(encodings);
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

  public List<Encoding> getEncodings() {
    return encodings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MediaType other = (MediaType) o;
    return
            Objects.equals(mediaType, other.mediaType) &&
            Objects.equals(model, other.model) &&
            examples.equals(other.examples) &&
            encodings.equals(other.encodings) &&
            vendorExtensions.equals(other.vendorExtensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mediaType, model, examples, vendorExtensions);
  }
}
