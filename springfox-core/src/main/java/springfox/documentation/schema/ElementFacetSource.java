package springfox.documentation.schema;

import java.util.Optional;

public interface ElementFacetSource {
  <T extends ElementFacet> Optional<T> elementFacet(Class<T> clazz);
}
