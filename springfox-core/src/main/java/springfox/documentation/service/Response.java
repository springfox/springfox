package springfox.documentation.service;

import springfox.documentation.schema.Example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @since 3.0.0
 */
public class Response {
  private final String code;
  private final String description;
  private final boolean isDefault;
  private final Set<Representation> representations = new HashSet<>();
  private final List<Header> headers = new ArrayList<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();
  private final List<Example> examples = new ArrayList<>();

  public Response(
      String code,
      String description,
      boolean isDefault,
      Collection<Header> headers,
      Collection<Representation> representations,
      Collection<Example> examples,
      Collection<VendorExtension> vendorExtensions) {
    this.code = code;
    this.description = description;
    this.isDefault = isDefault;
    this.representations.addAll(representations);
    this.headers.addAll(headers);
    this.vendorExtensions.addAll(vendorExtensions);
    this.examples.addAll(examples);
  }

  public SortedSet<Representation> getRepresentations() {
    return representations.stream()
        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Representation::getMediaType))));
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

  public List<Example> getExamples() {
    return examples;
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

  @Override
  public String toString() {
    return "Response{" +
        "code='" + code + '\'' +
        ", description='" + description + '\'' +
        ", isDefault=" + isDefault +
        ", representations=" + representations +
        ", headers=" + headers +
        ", vendorExtensions=" + vendorExtensions +
        ", examples=" + examples +
        '}';
  }
}
