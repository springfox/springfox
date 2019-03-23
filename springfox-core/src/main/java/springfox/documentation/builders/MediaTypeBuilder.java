package springfox.documentation.builders;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.Model;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.VendorExtension;

import java.util.List;

public class MediaTypeBuilder {
  private Model model;
  private List<Example> examples;
  private List<VendorExtension> vendorExtensions;

  public MediaTypeBuilder withModel(Model model) {
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

  public MediaType createMediaType() {
    return new MediaType(model, examples, vendorExtensions);
  }
}