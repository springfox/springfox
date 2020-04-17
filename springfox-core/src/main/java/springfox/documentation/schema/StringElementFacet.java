package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;
import springfox.documentation.builders.StringElementFacetBuilder;

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
}
