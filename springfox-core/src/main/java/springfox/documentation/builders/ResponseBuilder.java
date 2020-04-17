package springfox.documentation.builders;

import springfox.documentation.schema.Example;
import springfox.documentation.service.Header;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.Response;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseBuilder {
  private String code;
  private String description;
  private Boolean isDefault = false;
  private final List<Header> headers = new ArrayList<>();
  private final Set<MediaType> mediaTypes = new HashSet<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();
  private final List<Example> examples = new ArrayList<>();

  public ResponseBuilder code(String code) {
    this.code = code;
    return this;
  }

  public ResponseBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ResponseBuilder isDefault(boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  public ResponseBuilder headers(Collection<Header> headers) {
    this.headers.addAll(headers);
    return this;
  }

  public ResponseBuilder mediaTypes(Set<MediaType> mediaTypes) {
    this.mediaTypes.addAll(mediaTypes);
    return this;
  }

  public ResponseBuilder vendorExtensions(Collection<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public ResponseBuilder examples(Collection<Example> examples) {
    this.examples.addAll(examples);
    return this;
  }

  public Response build() {
    return new Response(
        code,
        description,
        isDefault,
        headers,
        mediaTypes,
        examples,
        vendorExtensions);
  }
}