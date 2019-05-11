package springfox.documentation.schema;

public class CollectionElementFacet implements ElementFacet {
  private final Integer maxItems;
  private final Integer minItems;
  private final Boolean uniqueItems;

  public CollectionElementFacet(
      Integer maxItems,
      Integer minItems,
      Boolean uniqueItems) {
    this.maxItems = maxItems;
    this.minItems = minItems;
    this.uniqueItems = uniqueItems;
  }

  public Integer getMaxItems() {
    return maxItems;
  }

  public Integer getMinItems() {
    return minItems;
  }

  public Boolean getUniqueItems() {
    return uniqueItems;
  }
}
