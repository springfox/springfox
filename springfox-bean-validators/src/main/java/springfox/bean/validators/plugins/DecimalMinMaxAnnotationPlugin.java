/*
 *
 *  Copyright 2015 the original author or authors.
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import static springfox.bean.validators.plugins.BeanValidators.validatorFromBean;
import static springfox.bean.validators.plugins.BeanValidators.validatorFromField;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class DecimalMinMaxAnnotationPlugin implements ModelPropertyBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(DecimalMinMaxAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<DecimalMin> min = extractMin(context);
    Optional<DecimalMax> max = extractMax(context);

    // add support for @DecimalMin/@DecimalMax
    context.getBuilder().allowableValues(createAllowableValuesFromDecimalMinMaxForNumbers(min, max));

  }

  @VisibleForTesting
  Optional<DecimalMin> extractMin(ModelPropertyContext context) {
    return validatorFromBean(context, DecimalMin.class)
        .or(validatorFromField(context, DecimalMin.class));
  }

  @VisibleForTesting
  Optional<DecimalMax> extractMax(ModelPropertyContext context) {
    return validatorFromBean(context, DecimalMax.class)
        .or(validatorFromField(context, DecimalMax.class));
  }


  private AllowableValues createAllowableValuesFromDecimalMinMaxForNumbers(Optional<DecimalMin> decimalMin, Optional<DecimalMax> decimalMax) {
    AllowableRangeValues myvalues = null;

    if (decimalMin.isPresent() && decimalMax.isPresent()) {
      LOG.debug("@DecimalMin+@DecimalMax detected: adding AllowableRangeValues to field ");
      DecimalMin min = decimalMin.get();
      DecimalMax max = decimalMax.get();
      myvalues = new AllowableRangeValues(min.value(), !min.inclusive(), max.value(), !max.inclusive());

    } else if (decimalMin.isPresent()) {
      LOG.debug("@DecimalMin detected: adding AllowableRangeValues to field ");
      DecimalMin min = decimalMin.get();
      // use Max value until "infinity" works
      myvalues = new AllowableRangeValues(min.value(), !min.inclusive(), Double.toString(Double.MAX_VALUE), false);

    } else if (decimalMax.isPresent()) {
      // use Min value until "infinity" works
      LOG.debug("@DecimalMax detected: adding AllowableRangeValues to field ");
      DecimalMax max = decimalMax.get();
      myvalues = new AllowableRangeValues(Double.toString(-Double.MAX_VALUE), false, max.value(), !max.inclusive());

    }
    return myvalues;
  }

}
