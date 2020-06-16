package springfox.documentation.service;

import org.springframework.util.MimeType;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ContentSpecification {
  private final Map<org.springframework.http.MediaType, Representation> representations
      = new TreeMap<>(MimeType::compareTo);
  private final boolean requestBody;

  public ContentSpecification(
      boolean requestBody,
      Set<Representation> representations) {
    this.requestBody = requestBody;
    this.representations.putAll(representations.stream()
                                               .collect(Collectors.toMap(Representation::getMediaType, m -> m)));
  }

  public Set<Representation> getRepresentations() {
    Set<Representation> representations = new TreeSet<>(Comparator.comparing(Representation::getMediaType));
    representations.addAll(this.representations.values());
    return representations;
  }

  public Optional<Representation> representationFor(org.springframework.http.MediaType mediaType) {
    return Optional.ofNullable(representations.get(mediaType));
  }

  public boolean isRequestBody() {
    return requestBody;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentSpecification that = (ContentSpecification) o;
    return representations.equals(that.representations)
        && requestBody == that.requestBody;
  }

  @Override
  public int hashCode() {
    return Objects.hash(representations, requestBody);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ContentSpecification.class.getSimpleName() + "[", "]")
        .add("mediaTypes=" + representations)
        .add("requestBody=" + requestBody)
        .toString();
  }
}
