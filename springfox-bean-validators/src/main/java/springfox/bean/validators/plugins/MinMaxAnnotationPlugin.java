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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static springfox.bean.validators.plugins.BeanValidators.*;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class MinMaxAnnotationPlugin implements ModelPropertyBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(MinMaxAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Min> min = extractMin(context);
    Optional<Max> max = extractMax(context);

    // add support for @Min/@Max
    context.getBuilder().allowableValues(createAllowableValuesFromMinMaxForNumbers(min, max));

  }

  @VisibleForTesting
  Optional<Min> extractMin(ModelPropertyContext context) {
    return validatorFromBean(context, Min.class)
        .or(validatorFromField(context, Min.class));
  }

  @VisibleForTesting
  Optional<Max> extractMax(ModelPropertyContext context) {
    return validatorFromBean(context, Max.class)
        .or(validatorFromField(context, Max.class));
  }


  private AllowableValues createAllowableValuesFromMinMaxForNumbers(Optional<Min> min, Optional<Max> max) {
    AllowableRangeValues myvalues = null;

    if (min.isPresent() && max.isPresent()) {
      LOG.debug("@Min+@Max detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(Double.toString(min.get().value()), false, Double.toString(max.get().value()), false);

    } else if (min.isPresent()) {
      LOG.debug("@Min detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(Double.toString(min.get().value()), false, null, null);

    } else if (max.isPresent()) {
      LOG.debug("@Max detected: adding AllowableRangeValues to field ");
      myvalues = new AllowableRangeValues(null, null, Double.toString(max.get().value()), false);

    }
    return myvalues;
  }


}
