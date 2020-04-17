package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;

public interface ElementFacetBuilder {
  <T> T yield(Class<T> parentClazz);
  ElementFacet build();
  ElementFacetBuilder copyOf(ElementFacet facet);
}
