package springfox.documentation.schema;

import springfox.documentation.service.MediaType;

import java.util.Set;

public class ContentSpecificationBuilder {
  private Set<MediaType> mediaTypes;

  public ContentSpecificationBuilder withMediaTypes(Set<MediaType> mediaTypes) {
    this.mediaTypes = mediaTypes;
    return this;
  }

  public ContentSpecification createContentSpecification() {
    return new ContentSpecification(mediaTypes);
  }
}