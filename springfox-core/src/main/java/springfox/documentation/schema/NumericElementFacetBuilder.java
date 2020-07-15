package springfox.documentation.schema;

import springfox.documentation.builders.ElementFacetBuilder;
import springfox.documentation.service.AllowableRangeValues;

import java.math.BigDecimal;
import springfox.documentation.service.AllowableValues;

public class NumericElementFacetBuilder implements ElementFacetBuilder {
  private BigDecimal multipleOf;
  private BigDecimal minimum;
  private Boolean exclusiveMinimum;
  private BigDecimal maximum;
  private Boolean exclusiveMaximum;

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

  public NumericElementFacet build() {
    if (multipleOf == null
    && maximum == null
    && exclusiveMaximum == null
    && minimum == null
    && exclusiveMinimum == null) {
      return null;
    }
    return new NumericElementFacet(
        multipleOf,
        minimum,
        exclusiveMinimum,
        maximum,
        exclusiveMaximum);
  }

  @Override
  public NumericElementFacetBuilder copyOf(ElementFacet facet) {
    if (!(facet instanceof NumericElementFacet)) {
      return this;
    }
    NumericElementFacet other = (NumericElementFacet) facet;
    return this.exclusiveMaximum(other.getExclusiveMaximum())
               .exclusiveMinimum(other.getExclusiveMinimum())
               .maximum(other.getMaximum())
               .minimum(other.getMinimum())
               .multipleOf(other.getMultipleOf());
  }

  public NumericElementFacetBuilder from(AllowableValues allowableValues) {
    if (!(allowableValues instanceof AllowableRangeValues)) {
      return this;
    }
    AllowableRangeValues range = (AllowableRangeValues) allowableValues;
    return this.exclusiveMaximum(range.getExclusiveMax())
               .exclusiveMinimum(range.getExclusiveMin())
               .maximum(range.getMax() != null ? new BigDecimal(range.getMax()) : null)
               .minimum(range.getMin() != null ? new BigDecimal(range.getMin()) : null);
  }
}
