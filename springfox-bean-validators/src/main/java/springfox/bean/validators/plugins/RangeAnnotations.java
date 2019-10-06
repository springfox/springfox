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

import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.service.AllowableRangeValues;

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

  public static Optional<AllowableRangeValues> allowableRange(
      Optional<Min> min,
      Optional<Positive> positive,
      Optional<PositiveOrZero> positiveOrZero,
      Optional<Max> max,
      Optional<Negative> negative,
      Optional<NegativeOrZero> negativeOrZero) {
    Long minValue = minValue(min, positive, positiveOrZero);
    Long maxValue = maxValue(max, negative, negativeOrZero);

    AllowableRangeValues myvalues = null;

    if (minValue != null && maxValue != null) {
      LOG.debug("@Min / @Positive / @PositiveOrZero + @Max / @Negative / @NegativeOrZero detected: "
          + "adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          Double.toString(minValue),
          false,
          Double.toString(maxValue),
          false);

    } else if (minValue != null) {
      LOG.debug("@Min / @Positive / @PositiveOrZero detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          Double.toString(minValue),
          false,
          null,
          null);

    } else if (maxValue != null) {
      LOG.debug("@Max / @Negative / @NegativeOrZero detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(
          null,
          null,
          Double.toString(maxValue),
          false);
    }
    return Optional.ofNullable(myvalues);
  }

  private static Long maxValue(final Optional<Max> max, final Optional<Negative> negative,
      final Optional<NegativeOrZero> negativeOrZero) {
    final Long maxValue;
    if (negativeOrZero.isPresent()) {
      maxValue = 0L;
    } else if (negative.isPresent()) {
      maxValue = -1L;
    } else {
      maxValue = max.map(Max::value).orElse(null);
    }
    return maxValue;
  }

  private static Long minValue(final Optional<Min> min, final Optional<Positive> positive,
      final Optional<PositiveOrZero> positiveOrZero) {
    final Long minValue;
    if (positiveOrZero.isPresent()) {
      minValue = 0L;
    } else if (positive.isPresent()) {
      minValue = 1L;
    } else {
      minValue = min.map(Min::value).orElse(null);
    }
    return minValue;
  }
}
