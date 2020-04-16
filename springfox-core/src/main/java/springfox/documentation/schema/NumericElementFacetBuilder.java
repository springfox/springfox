package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;

import java.math.BigDecimal;

public class NumericElementFacetBuilder implements ElementFacetBuilder {
  private BigDecimal multipleOf;
  private BigDecimal minimum;
  private Boolean exclusiveMinimum;
  private BigDecimal maximum;
  private Boolean exclusiveMaximum;
  private final Object parentObject;

  public NumericElementFacetBuilder() {
    this(null);
  }

  public NumericElementFacetBuilder(Object parentObject) {
    this.parentObject = parentObject;
  }

  public NumericElementFacetBuilder multipleOf(BigDecimal multipleOf) {
    this.multipleOf = multipleOf;
    return this;
  }

  public NumericElementFacetBuilder minimum(BigDecimal minimum) {
    this.minimum = minimum;
    return this;
  }

  public NumericElementFacetBuilder exclusiveMinimum(Boolean exclusiveMinimum) {
    this.exclusiveMinimum = exclusiveMinimum;
    return this;
  }

  public NumericElementFacetBuilder maximum(BigDecimal maximum) {
    this.maximum = maximum;
    return this;
  }

  public NumericElementFacetBuilder exclusiveMaximum(Boolean exclusiveMaximum) {
    this.exclusiveMaximum = exclusiveMaximum;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T yield(Class<T> parentClazz) {
    return (T) parentObject;
  }

  public NumericElementFacet build() {
    return new NumericElementFacet(multipleOf, minimum, exclusiveMinimum, maximum, exclusiveMaximum);
  }

  public NumericElementFacetBuilder copyOf(NumericElementFacet other) {
    if (other == null) {
      return this;
    }
    return this.exclusiveMaximum(other.getExclusiveMaximum())
        .exclusiveMinimum(other.getExclusiveMinimum())
        .maximum(other.getMaximum())
        .minimum(other.getMinimum())
        .multipleOf(other.getMultipleOf());
  }
}