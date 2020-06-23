package springfox.documentation.builders;

import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.service.CollectionFormat;

import static springfox.documentation.builders.BuilderDefaults.*;

public class CollectionElementFacetBuilder implements ElementFacetBuilder {
  private Integer maxItems;
  private Integer minItems;
  private Boolean uniqueItems;
  private CollectionFormat collectionFormat;


  public CollectionElementFacetBuilder collectionFormat(CollectionFormat collectionFormat) {
    this.collectionFormat = defaultIfAbsent(collectionFormat, this.collectionFormat);
    return this;
  }

  public CollectionElementFacetBuilder maxItems(Integer maxItems) {
    this.maxItems = defaultIfAbsent(maxItems, this.maxItems);
    return this;
  }

  public CollectionElementFacetBuilder minItems(Integer minItems) {
    this.minItems = defaultIfAbsent(minItems, this.minItems);
    return this;
  }

  public CollectionElementFacetBuilder uniqueItems(Boolean uniqueItems) {
    this.uniqueItems = defaultIfAbsent(uniqueItems, this.uniqueItems);
    return this;
  }

  @Override
  public ElementFacet build() {
    if (maxItems == null && minItems == null && uniqueItems == null) {
      return null;
    }
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