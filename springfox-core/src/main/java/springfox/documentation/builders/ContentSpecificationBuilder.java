package springfox.documentation.builders;

import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.MediaType;

import java.util.Set;

public class ContentSpecificationBuilder {
  private final RequestParameterBuilder owner;
  private Set<MediaType> mediaTypes;

  ContentSpecificationBuilder(RequestParameterBuilder owner) {
    this.owner = owner;
  }

  public ContentSpecificationBuilder mediaTypes(Set<MediaType> mediaTypes) {
    this.mediaTypes = mediaTypes;
    return this;
  }

  ContentSpecification create() {
    return new ContentSpecification(mediaTypes);
  }

  public RequestParameterBuilder build() {
    return owner;
  }
}