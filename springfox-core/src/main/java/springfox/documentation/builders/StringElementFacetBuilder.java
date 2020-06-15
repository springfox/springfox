package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.StringElementFacet;

public class StringElementFacetBuilder implements ElementFacetBuilder {
  private Integer maxLength;
  private Integer minLength;
  private String pattern;

  public StringElementFacetBuilder maxLength(Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public StringElementFacetBuilder minLength(Integer minLength) {
    this.minLength = minLength;
    return this;
  }

  public StringElementFacetBuilder pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

  @Override
  public ElementFacet build() {
    if (maxLength == null && minLength == null && pattern == null) {
      return null;
    }
    return new StringElementFacet(maxLength, minLength, pattern);
  }

  @Override
  public StringElementFacetBuilder copyOf(ElementFacet facet) {
    if (!(facet instanceof StringElementFacet)) {
      return this;
    }
    StringElementFacet other = (StringElementFacet) facet;
    return this.minLength(other.getMinLength())
        .maxLength(other.getMaxLength())
        .pattern(other.getPattern());
  }
}