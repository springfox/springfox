package springfox.documentation.service;

import springfox.documentation.schema.Example;
import springfox.documentation.schema.Model;

import java.util.ArrayList;
import java.util.List;

public class MediaType {
  private final Model model;
  private final List<Example> examples = new ArrayList<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  public MediaType(
      Model model,
      List<Example> examples,
      List<VendorExtension> vendorExtensions) {
    this.model = model;
    this.examples.addAll(examples);
    this.vendorExtensions.addAll(vendorExtensions);
  }

  public Model getModel() {
    return model;
  }

  public List<Example> getExamples() {
    return examples;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }
}
