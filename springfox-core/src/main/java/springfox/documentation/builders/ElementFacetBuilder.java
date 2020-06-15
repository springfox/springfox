package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;

public interface ElementFacetBuilder {
  ElementFacet build();

  ElementFacetBuilder copyOf(ElementFacet facet);
}
