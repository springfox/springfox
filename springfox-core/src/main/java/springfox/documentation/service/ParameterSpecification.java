package springfox.documentation.service;

import java.util.Objects;
import java.util.Optional;

public class ParameterSpecification {
  private final SimpleParameterSpecification query;
  private final ContentSpecification content;

  public ParameterSpecification(
      SimpleParameterSpecification query,
      ContentSpecification content) {
    this.query = query;
    this.content = content;
  }

  public Optional<SimpleParameterSpecification> getQuery() {
    return Optional.ofNullable(query);
  }

  public Optional<ContentSpecification> getContent() {
    return Optional.ofNullable(content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParameterSpecification that = (ParameterSpecification) o;
    return Objects.equals(
        query,
        that.query) &&
        Objects.equals(
            content,
            that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        query,
        content);
  }

  @Override
  public String toString() {
    return "ParameterSpecification{" +
        "query=" + query +
        ", content=" + content +
        '}';
  }
}
