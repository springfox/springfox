package springfox.documentation.service;

import springfox.documentation.schema.Example;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @since 3.0.0
 */
public class Response {
  private final String code;
  private final String description;
  private final boolean isDefault;
  private final Set<Representation> representations;
  private final List<Header> headers;
  private final List<VendorExtension> vendorExtensions;
  private final List<Example> examples;

  public Response(
      String code,
      String description,
      boolean isDefault,
      List<Header> headers,
      Set<Representation> representations,
      List<Example> examples,
      List<VendorExtension> vendorExtensions) {
    this.code = code;
    this.description = description;
    this.isDefault = isDefault;
    this.representations = representations;
    this.headers = headers;
    this.vendorExtensions = vendorExtensions;
    this.examples = examples;
  }

  public Set<Representation> getRepresentations() {
    return representations;
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
    return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]")
        .add("code='" + code + "'")
        .add("description='" + description + "'")
        .add("isDefault=" + isDefault)
        .add("mediaTypes=" + representations)
        .add("headers=" + headers)
        .add("vendorExtensions=" + vendorExtensions)
        .add("examples=" + examples)
        .toString();
  }
}
