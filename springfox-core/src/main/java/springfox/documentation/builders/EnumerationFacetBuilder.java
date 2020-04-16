package springfox.documentation.builders;

import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumerationFacetBuilder implements ElementFacetBuilder {
  private final Set<String> allowedValues = new HashSet<>();
  private Object parent;

  public EnumerationFacetBuilder(Object parent) {
    this.parent = parent;
  }

  public EnumerationFacetBuilder allowedValues(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
    return this;
  }

  public EnumerationFacetBuilder allowedValues(AllowableValues allowedValues) {
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
    return new EnumerationFacet(allowedValues);
  }

  public static List<String> from(AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      return ((AllowableListValues) allowableValues).getValues();
    }
    return new ArrayList<>();
  }
}