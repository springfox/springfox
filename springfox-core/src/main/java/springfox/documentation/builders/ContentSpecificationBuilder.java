package springfox.documentation.builders;

import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.Representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentSpecificationBuilder {
  private final RequestParameterBuilder parent;
  private static final org.springframework.http.MediaType DEFAULT_MEDIA_TYPE =
      new org.springframework.http.MediaType("application", "springfox");
  private Map<org.springframework.http.MediaType, RepresentationBuilder> representations = new HashMap<>();
  private boolean requestBody = false;

  ContentSpecificationBuilder(RequestParameterBuilder parent) {
    this.parent = parent;
  }

  public ContentSpecificationBuilder representations(Collection<Representation> representations) {
    this.representations.putAll(representations.stream()
        .collect(Collectors.toMap(
            Representation::getMediaType,
            r -> representationBuilderFor(r.getMediaType()).copyOf(r))));
    return this;
  }

  public RepresentationBuilder representationBuilderFor(org.springframework.http.MediaType mediaType) {
    return this.representations.computeIfAbsent(mediaType,
        m -> new RepresentationBuilder(this)
            .mediaType(m));
  }

  public ContentSpecificationBuilder requestBody(boolean requestBody) {
    this.requestBody = requestBody;
    return this;
  }

  ContentSpecification build() {
    return new ContentSpecification(
        requestBody,
        representations.values()
            .stream()
            .map(RepresentationBuilder::build)
            .collect(Collectors.toSet()));
  }

  public RequestParameterBuilder yield() {
    return parent;
  }

  public ContentSpecificationBuilder copyOf(ContentSpecification contentParameter) {
    if (contentParameter != null) {
      this.representations(contentParameter.getRepresentations())
          .requestBody(contentParameter.isRequestBody());
    }
    return this;
  }
}