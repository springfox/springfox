package springfox.documentation.schema;

import springfox.documentation.service.MediaType;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ContentSpecification {
  private final SortedSet<MediaType> mediaTypes = new TreeSet<>(Comparator.comparing(MediaType::getMediaType));

  public ContentSpecification(Set<MediaType> mediaTypes) {
    this.mediaTypes.addAll(mediaTypes);
  }

  public SortedSet<MediaType> getMediaTypes() {
    return mediaTypes;
  }
}
