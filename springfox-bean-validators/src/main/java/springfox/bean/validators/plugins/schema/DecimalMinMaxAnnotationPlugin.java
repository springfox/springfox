/*
 *
 *  Copyright 2015-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.bean.validators.plugins.schema;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.NumericElementFacetBuilder;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Optional;

import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class DecimalMinMaxAnnotationPlugin implements ModelPropertyBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(DecimalMinMaxAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void apply(ModelPropertyContext context) {
    Optional<DecimalMin> min = extractAnnotation(context, DecimalMin.class);
    Optional<DecimalMax> max = extractAnnotation(context, DecimalMax.class);

    // add support for @DecimalMin/@DecimalMax
    Compatibility<AllowableValues, NumericElementFacet> values =
        facetFromDecimalMinMaxForNumbers(min, max);
    context.getBuilder().allowableValues(values.getLegacy().orElse(null));
    values.getModern()
          .ifPresent(facet -> context.getSpecificationBuilder()
                                     .numericFacet(n -> n.copyOf(facet)));

  }

  private Compatibility<AllowableValues, NumericElementFacet> facetFromDecimalMinMaxForNumbers(
      Optional<DecimalMin> decimalMin,
      Optional<DecimalMax> decimalMax) {
    AllowableRangeValues myvalues = null;
    NumericElementFacet numericFacet = null;

    if (decimalMin.isPresent() && decimalMax.isPresent()) {
      LOG.debug("@DecimalMin+@DecimalMax detected: adding AllowableRangeValues to field ");
      DecimalMin min = decimalMin.get();
      DecimalMax max = decimalMax.get();
      myvalues = new AllowableRangeValues(min.value(), !min.inclusive(), max.value(), !max.inclusive());
      numericFacet = new NumericElementFacetBuilder()
          .multipleOf(NumericElementFacet.DEFAULT_MULTIPLE)
          .minimum(new BigDecimal(min.value()))
          .exclusiveMinimum(!min.inclusive())
          .maximum(new BigDecimal(max.value()))
          .exclusiveMaximum(!max.inclusive())
          .build();

    } else if (decimalMin.isPresent()) {
      LOG.debug("@DecimalMin detected: adding AllowableRangeValues to field ");
      DecimalMin min = decimalMin.get();
      myvalues = new AllowableRangeValues(min.value(), !min.inclusive(), null, null);
      numericFacet = new NumericElementFacetBuilder()
          .multipleOf(NumericElementFacet.DEFAULT_MULTIPLE)
          .minimum(new BigDecimal(min.value()))
          .exclusiveMinimum(!min.inclusive())
          .maximum(null)
          .exclusiveMaximum(null)
          .build();

    } else if (decimalMax.isPresent()) {
      LOG.debug("@DecimalMax detected: adding AllowableRangeValues to field ");
      DecimalMax max = decimalMax.get();
      myvalues = new AllowableRangeValues(null, null, max.value(), !max.inclusive());
      numericFacet = new NumericElementFacetBuilder()
          .multipleOf(NumericElementFacet.DEFAULT_MULTIPLE)
          .minimum(null)
          .exclusiveMinimum(null)
          .maximum(new BigDecimal(max.value()))
          .exclusiveMaximum(!max.inclusive())
          .build();

    }
    return new Compatibility<>(myvalues, numericFacet);
  }

}
