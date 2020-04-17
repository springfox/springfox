package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

public class NumericElementFacet implements ElementFacet {
  public static final BigDecimal DEFAULT_MULTIPLE = BigDecimal.ONE;
  private final BigDecimal multipleOf;
  private final BigDecimal maximum;
  private final Boolean exclusiveMaximum;
  private final BigDecimal minimum;
  private final Boolean exclusiveMinimum;

  public NumericElementFacet(
      BigDecimal multipleOf,
      BigDecimal minimum,
      Boolean exclusiveMinimum,
      BigDecimal maximum,
      Boolean exclusiveMaximum) {
    this.multipleOf = multipleOf;
    this.maximum = maximum;
    this.exclusiveMaximum = exclusiveMaximum;
    this.minimum = minimum;
    this.exclusiveMinimum = exclusiveMinimum;
  }

  public BigDecimal getMultipleOf() {
    return multipleOf;
  }

  public BigDecimal getMaximum() {
    return maximum;
  }

  public Boolean getExclusiveMaximum() {
    return exclusiveMaximum;
  }

  public BigDecimal getMinimum() {
    return minimum;
  }

  public Boolean getExclusiveMinimum() {
    return exclusiveMinimum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NumericElementFacet that = (NumericElementFacet) o;
    return Objects.equals(multipleOf, that.multipleOf) &&
        Objects.equals(maximum, that.maximum) &&
        Objects.equals(exclusiveMaximum, that.exclusiveMaximum) &&
        Objects.equals(minimum, that.minimum) &&
        Objects.equals(exclusiveMinimum, that.exclusiveMinimum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multipleOf, maximum, exclusiveMaximum, minimum, exclusiveMinimum);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", NumericElementFacet.class.getSimpleName() + "[", "]")
        .add("multipleOf=" + multipleOf)
        .add("maximum=" + maximum)
        .add("exclusiveMaximum=" + exclusiveMaximum)
        .add("minimum=" + minimum)
        .add("exclusiveMinimum=" + exclusiveMinimum)
        .toString();
  }

  @Override
  public Class<? extends ElementFacetBuilder> facetBuilder() {
    return NumericElementFacetBuilder.class;
  }
}
