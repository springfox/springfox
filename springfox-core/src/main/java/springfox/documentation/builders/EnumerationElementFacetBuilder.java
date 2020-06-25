package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnumerationElementFacetBuilder implements ElementFacetBuilder {
  private final Set<String> allowedValues = new HashSet<>();

  public EnumerationElementFacetBuilder allowedValues(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
    return this;
  }

  public EnumerationElementFacetBuilder allowedValues(AllowableValues allowedValues) {
    if (!from(allowedValues).isEmpty()) {
      this.allowedValues.clear();
      this.allowedValues.addAll(from(allowedValues));
    }
    return this;
  }

  @Override
  public EnumerationFacet build() {
    if (allowedValues.isEmpty()) {
      return null;
    }
    return new EnumerationFacet(allowedValues);
  }

  @Override
  public EnumerationElementFacetBuilder copyOf(ElementFacet facet) {
    if (!(facet instanceof EnumerationFacet)) {
      return this;
    }
    EnumerationFacet other = (EnumerationFacet) facet;
    return this.allowedValues(other.getAllowedValues());
  }

  public static Set<String> from(AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      return new HashSet<>(((AllowableListValues) allowableValues).getValues());
    }
    return new HashSet<>();
  }
}