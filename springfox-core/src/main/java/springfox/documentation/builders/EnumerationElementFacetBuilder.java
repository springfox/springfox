package springfox.documentation.builders;

import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumerationElementFacetBuilder implements ElementFacetBuilder {
  private final Set<String> allowedValues = new HashSet<>();
  private Object parent;

  public EnumerationElementFacetBuilder(Object parent) {
    this.parent = parent;
  }

  public EnumerationElementFacetBuilder allowedValues(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
    return this;
  }

  public EnumerationElementFacetBuilder allowedValues(AllowableValues allowedValues) {
    this.allowedValues.addAll(from(allowedValues));
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T yield(Class<T> parentClazz) {
    return (T) parent;
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

  public static List<String> from(AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      return ((AllowableListValues) allowableValues).getValues();
    }
    return new ArrayList<>();
  }
}