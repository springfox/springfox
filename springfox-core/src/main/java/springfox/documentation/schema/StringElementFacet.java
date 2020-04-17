package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;
import springfox.documentation.builders.StringElementFacetBuilder;

import java.util.Objects;
import java.util.StringJoiner;

public class StringElementFacet implements ElementFacet {
  private final Integer maxLength;
  private final Integer minLength;
  private final String pattern;

  public StringElementFacet(
      Integer maxLength,
      Integer minLength,
      String pattern) {
    this.maxLength = maxLength;
    this.minLength = minLength;
    this.pattern = pattern;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public Integer getMinLength() {
    return minLength;
  }

  public String getPattern() {
    return pattern;
  }

  @Override
  public Class<? extends ElementFacetBuilder> facetBuilder() {
    return StringElementFacetBuilder.class;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StringElementFacet that = (StringElementFacet) o;
    return Objects.equals(maxLength, that.maxLength) &&
        Objects.equals(minLength, that.minLength) &&
        Objects.equals(pattern, that.pattern);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxLength, minLength, pattern);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", StringElementFacet.class.getSimpleName() + "[", "]")
        .add("maxLength=" + maxLength)
        .add("minLength=" + minLength)
        .add("pattern='" + pattern + "'")
        .toString();
  }
}
