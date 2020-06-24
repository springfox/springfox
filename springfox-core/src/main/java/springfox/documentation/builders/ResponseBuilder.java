package springfox.documentation.builders;

import org.springframework.http.MediaType;
import springfox.documentation.schema.Example;
import springfox.documentation.service.Header;
import springfox.documentation.service.Response;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ResponseBuilder {
  private String code;
  private String description;
  private Boolean isDefault = false;
  private final List<Header> headers = new ArrayList<>();
  private final Map<MediaType, RepresentationBuilder> representations = new HashMap<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();
  private final List<Example> examples = new ArrayList<>();

  public ResponseBuilder code(String code) {
    this.code = defaultIfAbsent(code, this.code);
    return this;
  }

  public ResponseBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
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

  private RepresentationBuilder representationBuilderFor(org.springframework.http.MediaType mediaType) {
    return this.representations.computeIfAbsent(mediaType,
        m -> new RepresentationBuilder()
            .mediaType(m));
  }

  public Function<Consumer<RepresentationBuilder>, ResponseBuilder> representation(
      org.springframework.http.MediaType mediaType) {
    return content -> {
      content.accept(representationBuilderFor(mediaType));
      return this;
    };
  }

  public ResponseBuilder vendorExtensions(Collection<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public ResponseBuilder examples(Collection<Example> examples) {
    this.examples.addAll(examples);
    return this;
  }

  public ResponseBuilder copyOf(Response source) {
    if (source == null) {
      return this;
    }
    source.getRepresentations().forEach(each ->
        this.representation(each.getMediaType()).apply(r -> r.copyOf(each)));
    this.code(source.getCode())
        .description(source.getDescription())
        .examples(source.getExamples())
        .headers(source.getHeaders())
        .isDefault(source.isDefault())
        .vendorExtensions(source.getVendorExtensions());
    return this;
  }

  public Response build() {
    return new Response(
        code,
        description,
        isDefault,
        headers,
        representations.values()
            .stream()
            .map(RepresentationBuilder::build)
            .collect(Collectors.toSet()),
        examples,
        vendorExtensions);
  }
}