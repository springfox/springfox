package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;

public interface ElementFacet {
  Class<? extends ElementFacetBuilder> facetBuilder();
}
