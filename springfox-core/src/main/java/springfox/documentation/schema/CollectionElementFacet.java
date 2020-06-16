package springfox.documentation.schema;

import springfox.documentation.builders.CollectionElementFacetBuilder;
import springfox.documentation.builders.ElementFacetBuilder;

import java.util.Objects;
import java.util.StringJoiner;


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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionElementFacet that = (CollectionElementFacet) o;
    return Objects.equals(maxItems, that.maxItems) &&
        Objects.equals(minItems, that.minItems) &&
        Objects.equals(uniqueItems, that.uniqueItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxItems, minItems, uniqueItems);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CollectionElementFacet.class.getSimpleName() + "[", "]")
        .add("maxItems=" + maxItems)
        .add("minItems=" + minItems)
        .add("uniqueItems=" + uniqueItems)
        .toString();
  }
}
