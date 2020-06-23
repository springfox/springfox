package springfox.documentation.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @since 3.0.0
 */
public class RequestBody {
  private final String description;
  private final Set<Representation> representations = new TreeSet<>(Comparator.comparing(Representation::getMediaType));
  private final Boolean required;
  private final List<VendorExtension> extensions;

  public RequestBody(
      String description,
      Set<Representation> representations,
      Boolean required,
      List<VendorExtension> extensions) {
    this.description = description;
    this.representations.addAll(representations);
    this.required = required;
    this.extensions = extensions;
  }

  public String getDescription() {
    return description;
  }

  public Collection<Representation> getRepresentations() {
    return representations;
  }

  public Boolean getRequired() {
    return required;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }
}
