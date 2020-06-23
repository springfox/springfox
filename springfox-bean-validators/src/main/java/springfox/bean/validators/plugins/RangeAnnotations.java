/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

package springfox.bean.validators.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.NumericElementFacetBuilder;
import springfox.documentation.service.AllowableRangeValues;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Optional;

import static springfox.documentation.schema.NumericElementFacet.*;

public class RangeAnnotations {
  private static final Logger LOG = LoggerFactory.getLogger(RangeAnnotations.class);

  private RangeAnnotations() {
    throw new UnsupportedOperationException();
  }

  public static AllowableRangeValues stringLengthRange(Size size) {
    LOG.debug("@Size detected: adding MinLength/MaxLength to field");
    return new AllowableRangeValues(minValue(size), maxValue(size));
  }

  private static String minValue(Size size) {
    return String.valueOf(Math.max(size.min(), 0));
  }

  private static String maxValue(Size size) {
    return String.valueOf(Math.max(0, Math.min(size.max(), Integer.MAX_VALUE)));
  }

  public static Compatibility<AllowableRangeValues, NumericElementFacet> allowableRange(
      Optional<Min> min,
      Optional<Max> max) {
    AllowableRangeValues myvalues = null;
    NumericElementFacet range = null;

    if (min.isPresent() && max.isPresent()) {
      LOG.debug("@Min+@Max detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          Double.toString(min.get().value()),
          false,
          Double.toString(max.get().value()),
          false);
      range = new NumericElementFacetBuilder()
          .multipleOf(DEFAULT_MULTIPLE)
          .minimum(BigDecimal.valueOf(min.get().value()))
          .exclusiveMinimum(false)
          .maximum(BigDecimal.valueOf(max.get().value()))
          .exclusiveMaximum(false)
          .build();

    } else if (min.isPresent()) {
      LOG.debug("@Min detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          Double.toString(min.get().value()),
          false,
          null,
          null);
      range = new NumericElementFacetBuilder()
          .multipleOf(DEFAULT_MULTIPLE)
          .minimum(BigDecimal.valueOf(min.get().value()))
          .exclusiveMinimum(false)
          .maximum(null)
          .exclusiveMaximum(null)
          .build();

    } else if (max.isPresent()) {
      LOG.debug("@Max detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          null,
          null,
          Double.toString(max.get().value()),
          false);
      range = new NumericElementFacetBuilder()
          .multipleOf(DEFAULT_MULTIPLE)
          .minimum(null)
          .exclusiveMinimum(null)
          .maximum(BigDecimal.valueOf(max.get().value()))
          .exclusiveMaximum(false)
          .build();
    }
    return new Compatibility<>(myvalues, range);
  }
}
