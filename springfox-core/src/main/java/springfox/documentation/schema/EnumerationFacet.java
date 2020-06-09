package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;
import springfox.documentation.builders.EnumerationElementFacetBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class EnumerationFacet implements ElementFacet {
  private final List<String> allowedValues = new ArrayList<>();

  public EnumerationFacet(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
    this.allowedValues.sort(Comparator.naturalOrder());
  }

  public List<String> getAllowedValues() {
    return allowedValues;
  }

  @Override
  public Class<? extends ElementFacetBuilder> facetBuilder() {
    return EnumerationElementFacetBuilder.class;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnumerationFacet that = (EnumerationFacet) o;
    return Objects.equals(allowedValues, that.allowedValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowedValues);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", EnumerationFacet.class.getSimpleName() + "[", "]")
        .add("allowedValues=" + allowedValues)
        .toString();
  }
}
