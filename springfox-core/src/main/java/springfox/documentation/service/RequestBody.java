package springfox.documentation.service;

import java.util.List;
import java.util.SortedSet;

/**
 * @since 3.0.0
 */
public class RequestBody {
  private final String description;
  private final SortedSet<MediaType> mediaTypes;
  private final Boolean required;
  private final List<VendorExtension> extensions;

  public RequestBody(
      String description,
      SortedSet<MediaType> mediaTypes,
      Boolean required,
      List<VendorExtension> extensions) {
    this.description = description;
    this.mediaTypes = mediaTypes;
    this.required = required;
    this.extensions = extensions;
  }

  public String getDescription() {
    return description;
  }

  public SortedSet<MediaType> getMediaTypes() {
    return mediaTypes;
  }

  public Boolean getRequired() {
    return required;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }
}
