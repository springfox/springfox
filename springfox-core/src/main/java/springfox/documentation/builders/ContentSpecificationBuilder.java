package springfox.documentation.builders;

import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.Representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContentSpecificationBuilder {
  private final Map<org.springframework.http.MediaType, RepresentationBuilder> representations = new HashMap<>();
  private boolean requestBody = false;

  private ContentSpecificationBuilder representations(Collection<Representation> representations) {
    this.representations.putAll(representations.stream()
        .collect(Collectors.toMap(
            Representation::getMediaType,
            r -> representationBuilderFor(r.getMediaType())
                .copyOf(r))));
    return this;
  }

  private RepresentationBuilder representationBuilderFor(org.springframework.http.MediaType mediaType) {
    return this.representations.computeIfAbsent(mediaType,
        m -> new RepresentationBuilder()
            .mediaType(m));
  }

  public Function<Consumer<RepresentationBuilder>, ContentSpecificationBuilder> representation(
      org.springframework.http.MediaType mediaType) {
    return content -> {
      content.accept(representationBuilderFor(mediaType));
      return this;
    };
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

  public ContentSpecificationBuilder copyOf(ContentSpecification contentParameter) {
    if (contentParameter != null) {
      this.representations(contentParameter.getRepresentations())
          .requestBody(contentParameter.isRequestBody());
    }
    return this;
  }
}