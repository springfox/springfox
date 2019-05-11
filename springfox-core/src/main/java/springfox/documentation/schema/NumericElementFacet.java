package springfox.documentation.schema;

import java.math.BigDecimal;

public class NumericElementFacet implements ElementFacet {
  private final BigDecimal multipleOf;
  private final BigDecimal maximum;
  private final Boolean exclusiveMaximum;
  private final BigDecimal minimum;
  private final Boolean exclusiveMinimum;

  public NumericElementFacet(
      BigDecimal multipleOf,
      BigDecimal maximum,
      Boolean exclusiveMaximum,
      BigDecimal minimum,
      Boolean exclusiveMinimum) {
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
}
