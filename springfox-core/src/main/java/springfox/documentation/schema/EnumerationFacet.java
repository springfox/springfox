package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;
import springfox.documentation.builders.EnumerationFacetBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumerationFacet implements ElementFacet {
  private final List<String> allowedValues = new ArrayList<>();

  public EnumerationFacet(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
  }

  public List<String> getAllowedValues() {
    return allowedValues;
  }

  @Override
  public Class<? extends ElementFacetBuilder> facetBuilder() {
    return EnumerationFacetBuilder.class;
  }
}
