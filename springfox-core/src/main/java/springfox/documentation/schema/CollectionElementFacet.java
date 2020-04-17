package springfox.documentation.schema;

import springfox.documentation.builders.CollectionElementFacetBuilder;
import springfox.documentation.builders.ElementFacetBuilder;


//TODO: add tests to excercise this facet
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

  @Override
  public Class<? extends ElementFacetBuilder> facetBuilder() {
    return CollectionElementFacetBuilder.class;
  }
}
