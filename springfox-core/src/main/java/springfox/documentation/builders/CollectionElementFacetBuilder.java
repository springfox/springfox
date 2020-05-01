package springfox.documentation.builders;

import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.service.CollectionFormat;

public class CollectionElementFacetBuilder implements ElementFacetBuilder {
  private final Object parent;
  private Integer maxItems;
  private Integer minItems;
  private Boolean uniqueItems;
  private CollectionFormat collectionFormat;

  public CollectionElementFacetBuilder(Object parent) {
    this.parent = parent;
  }

  public CollectionElementFacetBuilder collectionFormat(CollectionFormat collectionFormat) {
    this.collectionFormat = collectionFormat;
    return this;
  }

  public CollectionElementFacetBuilder maxItems(Integer maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  public CollectionElementFacetBuilder minItems(Integer minItems) {
    this.minItems = minItems;
    return this;
  }

  public CollectionElementFacetBuilder uniqueItems(Boolean uniqueItems) {
    this.uniqueItems = uniqueItems;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T yield(Class<T> parentClazz) {
    return (T) parent;
  }

  @Override
  public ElementFacet build() {
    return new CollectionElementFacet(maxItems, minItems, uniqueItems);
  }

  @Override
  public CollectionElementFacetBuilder copyOf(ElementFacet facet) {
    if (!(facet instanceof CollectionElementFacet)) {
      return this;
    }
    CollectionElementFacet other = (CollectionElementFacet) facet;
    return maxItems(other.getMaxItems())
        .minItems(other.getMinItems())
        .uniqueItems(other.getUniqueItems());
  }
}