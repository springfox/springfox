package springfox.documentation.builders;

import springfox.documentation.service.Encoding;
import springfox.documentation.service.Representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RepresentationBuilder {
  private final ContentSpecificationBuilder parent;
  private org.springframework.http.MediaType mediaType;
  private ModelSpecificationBuilder modelBuilder;
  private final Map<String, EncodingBuilder> encodings = new HashMap<>();

  public RepresentationBuilder(ContentSpecificationBuilder parent) {
    this.parent = parent;
  }

  public ModelSpecificationBuilder modelSpecificationBuilder(String sourceIdentifier) {
    if (modelBuilder == null) {
      this.modelBuilder = new ModelSpecificationBuilder(sourceIdentifier, this);
    }
    return modelBuilder;
  }

  public RepresentationBuilder mediaType(String mediaType) {
    this.mediaType = org.springframework.http.MediaType.parseMediaType(mediaType);
    return this;
  }

  public RepresentationBuilder mediaType(org.springframework.http.MediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public RepresentationBuilder encodings(Collection<Encoding> encodings) {
    if (encodings == null) {
      this.encodings.clear();
    } else {
      encodings.forEach(e -> this.encodings.put(
          e.getPropertyRef(),
          encodingForProperty(e.getPropertyRef())
              .copyOf(e)));
    }
    return this;
  }

  public EncodingBuilder encodingForProperty(String property) {
    //noinspection unchecked
    return this.encodings.computeIfAbsent(
        property,
        p -> new EncodingBuilder(this)
            .propertyRef(p));
  }

  public Representation build() {
    return new Representation(
        mediaType,
        modelBuilder.build(),
        encodings.values()
            .stream()
            .map(EncodingBuilder::build)
            .collect(Collectors.toSet()));
  }

  public ContentSpecificationBuilder yield() {
    return parent;
  }

  public RepresentationBuilder copyOf(Representation other) {
    if (other != null) {
      other.getEncodings().forEach(e -> this.modelSpecificationBuilder("aggregate")
          .copyOf(other.getModel())
          .yield(RepresentationBuilder.class)
          .encodingForProperty(e.getPropertyRef())
          .copyOf(e)
          .yield());

    }
    return this;
  }
}