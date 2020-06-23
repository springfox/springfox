package springfox.documentation.service;

import springfox.documentation.schema.ModelSpecification;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Representation {
  private final org.springframework.http.MediaType mediaType;
  private final ModelSpecification model;
  private final Set<Encoding> encodings = new TreeSet<>(Comparator.comparing(Encoding::getPropertyRef));

  public Representation(
      org.springframework.http.MediaType mediaType,
      ModelSpecification model,
      Set<Encoding> encodings) {
    this.mediaType = mediaType;
    this.model = model;
    this.encodings.addAll(encodings);
  }

  public ModelSpecification getModel() {
    return model;
  }

  public org.springframework.http.MediaType getMediaType() {
    return mediaType;
  }

  public Collection<Encoding> getEncodings() {
    return encodings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Representation other = (Representation) o;
    return
        Objects.equals(mediaType, other.mediaType) &&
            Objects.equals(model, other.model) &&
            encodings.equals(other.encodings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mediaType, model, encodings);
  }

  @Override
  public String toString() {
    return "Representation{" +
        "mediaType=" + mediaType +
        ", model=" + model +
        ", encodings=" + encodings +
        '}';
  }
}
