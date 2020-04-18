package springfox.documentation.builders;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.Encoding;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MediaTypeBuilder {
  private org.springframework.http.MediaType mediaType;
  private ModelSpecification model;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();
  private final List<Encoding> encodings = new ArrayList<>();

  public MediaTypeBuilder modelSpecification(ModelSpecification model) {
    this.model = model;
    return this;
  }

  public MediaTypeBuilder examples(Collection<Example> examples) {
    this.examples.addAll(examples);
    return this;
  }

  public MediaTypeBuilder mediaType(String mediaType) {
    this.mediaType = org.springframework.http.MediaType.parseMediaType(mediaType);
    return this;
  }

  public MediaTypeBuilder mediaType(org.springframework.http.MediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public MediaTypeBuilder extensions(Collection<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public MediaTypeBuilder encodings(Collection<Encoding> encodings) {
    this.encodings.addAll(encodings);
    return this;
  }

  public MediaType build() {
    return new MediaType(mediaType, model, examples, encodings, vendorExtensions);
  }
}