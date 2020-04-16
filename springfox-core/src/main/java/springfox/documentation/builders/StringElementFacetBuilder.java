package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.StringElementFacet;

public class StringElementFacetBuilder implements ElementFacetBuilder {
  private final Object parent;
  private Integer maxLength;
  private Integer minLength;
  private String pattern;
  
  public StringElementFacetBuilder(Object parent) {
    this.parent = parent;
  }

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

  @SuppressWarnings("unchecked")
  @Override
  public <T> T yield(Class<T> parentClazz) {
    return (T) parent;
  }

  @Override
  public ElementFacet build() {
    return new StringElementFacet(maxLength, minLength, pattern);
  }
}