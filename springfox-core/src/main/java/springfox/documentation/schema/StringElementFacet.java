package springfox.documentation.schema;

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
}
