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
package springfox.bean.validators.plugins.parameter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Optional;

import static springfox.bean.validators.plugins.RangeAnnotations.*;
import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class MinMaxAnnotationPlugin implements ParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(MinMaxAnnotationPlugin.class);

  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @SuppressWarnings("deprecation")
  public void apply(ParameterContext context) {
    Optional<Min> min = annotationFromParameter(context, Min.class);
    Optional<Max> max = annotationFromParameter(context, Max.class);
    if (min.isPresent() || max.isPresent()) {
      Compatibility<AllowableRangeValues, NumericElementFacet> values = allowableRange(min, max);
      LOG.debug(String.format(
          "Adding allowable Values: %s",
          values.getLegacy().map(AllowableRangeValues::toString).orElse("<none>")));
      context.parameterBuilder().allowableValues(values.getLegacy().orElse(null));
      LOG.debug(String.format(
          "Adding numeric element facet : %s",
          values.getModern().map(NumericElementFacet::toString).orElse("<none>")));
      context.requestParameterBuilder()
             .query(q -> q.numericFacet(n -> n.copyOf(values.getModern().orElse(null))));
    }
  }

}
