/*
 *
 *  Copyright 2016-2017 the original author or authors.
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
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Optional;

import static springfox.bean.validators.plugins.RangeAnnotations.*;
import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class MinMaxAnnotationPlugin implements ModelPropertyBuilderPlugin {
  private static final Logger LOGGER = LoggerFactory.getLogger(MinMaxAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void apply(ModelPropertyContext context) {
    Optional<Min> min = extractMin(context);
    Optional<Max> max = extractMax(context);

    // add support for @Min/@Max
    Compatibility<AllowableRangeValues, NumericElementFacet> values = allowableRange(min, max);
    LOGGER.debug(String.format(
        "Adding allowable Values: %s",
        values.getLegacy().map(AllowableRangeValues::toString).orElse("<none>")));
    context.getBuilder()
           .allowableValues(
               values.getLegacy()
                     .orElse(null));
    LOGGER.debug(String.format(
        "Adding numeric element facet : %s",
        values.getModern().map(NumericElementFacet::toString).orElse("<none>")));
    values.getModern()
          .ifPresent(facet -> context.getSpecificationBuilder()
                                     .numericFacet(n -> n.copyOf(facet)));

  }

  private Optional<Min> extractMin(ModelPropertyContext context) {
    return annotationFromBean(context, Min.class).map(Optional::of).orElse(annotationFromField(context, Min.class));
  }

  private Optional<Max> extractMax(ModelPropertyContext context) {
    return annotationFromBean(context, Max.class).map(Optional::of).orElse(annotationFromField(context, Max.class));
  }
}
