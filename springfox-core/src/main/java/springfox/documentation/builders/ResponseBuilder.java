package springfox.documentation.builders;

import springfox.documentation.service.Header;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.Response;
import springfox.documentation.service.VendorExtension;

import java.util.List;
import java.util.Set;

public class ResponseBuilder {
  private String code;
  private String description;
  private boolean isDefault;
  private List<Header> headers;
  private Set<MediaType> mediaTypes;
  private List<VendorExtension> vendorExtensions;

  public ResponseBuilder withCode(String code) {
    this.code = code;
    return this;
  }

  public ResponseBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ResponseBuilder withIsDefault(boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  public ResponseBuilder withHeaders(List<Header> headers) {
    this.headers = headers;
    return this;
  }

  public ResponseBuilder withMediaTypes(Set<MediaType> mediaTypes) {
    this.mediaTypes = mediaTypes;
    return this;
  }

  public ResponseBuilder withVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions = vendorExtensions;
    return this;
  }

  public Response createResponse() {
    return new Response(code, description, isDefault, headers, mediaTypes, vendorExtensions);
  }
}