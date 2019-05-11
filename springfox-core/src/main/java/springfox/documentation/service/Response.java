package springfox.documentation.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @since 3.0.0
 */
public class Response {
  private final String code;
  private final String description;
  private final boolean isDefault;
  private final Set<MediaType> mediaTypes;
  private final List<Header> headers;
  private final List<VendorExtension> vendorExtensions;

  public Response(
      String code,
      String description,
      boolean isDefault,
      List<Header> headers,
      Set<MediaType> mediaTypes,
      List<VendorExtension> vendorExtensions) {
    this.code = code;
    this.description = description;
    this.isDefault = isDefault;
    this.mediaTypes = mediaTypes;
    this.headers = headers;
    this.vendorExtensions = vendorExtensions;
  }

  public Set<MediaType> getMediaTypes() {
    return mediaTypes;
  }

  public List<Header> getHeaders() {
    return headers;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Response response = (Response) o;
    return Objects.equals(code, response.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
