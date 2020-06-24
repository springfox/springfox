package springfox.documentation.builders;

import springfox.documentation.common.ExternalDocumentation;
import springfox.documentation.service.VendorExtension;

import java.util.Collection;

public class ExternalDocumentationBuilder {
  private String url;
  private String description;
  private Collection<VendorExtension> extensions;

  public ExternalDocumentationBuilder url(String url) {
    this.url = url;
    return this;
  }

  public ExternalDocumentationBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ExternalDocumentationBuilder extensions(Collection<VendorExtension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public ExternalDocumentation build() {
    return new ExternalDocumentation(url, description, extensions);
  }
}