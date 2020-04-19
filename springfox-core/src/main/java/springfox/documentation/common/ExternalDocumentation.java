package springfox.documentation.common;

import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class ExternalDocumentation {
  private final String url;
  private final String description;
  private final List<VendorExtension> extensions = new ArrayList<>();

  public ExternalDocumentation(
      String url,
      String description,
      Collection<VendorExtension> extensions) {
    this.url = url;
    this.description = description;
    this.extensions.addAll(extensions);
  }

  public String getUrl() {
    return url;
  }

  public String getDescription() {
    return description;
  }

  public Collection<VendorExtension> getExtensions() {
    return extensions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalDocumentation that = (ExternalDocumentation) o;
    return Objects.equals(url, that.url) &&
        Objects.equals(description, that.description) &&
        Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, description, extensions);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ExternalDocumentation.class.getSimpleName() + "[", "]")
        .add("url='" + url + "'")
        .add("description='" + description + "'")
        .add("extensions=" + extensions)
        .toString();
  }
}
