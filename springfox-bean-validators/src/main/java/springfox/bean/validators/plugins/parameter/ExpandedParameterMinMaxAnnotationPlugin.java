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
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Optional;

import static springfox.bean.validators.plugins.RangeAnnotations.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ExpandedParameterMinMaxAnnotationPlugin implements ExpandedParameterBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterMinMaxAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ParameterExpansionContext context) {

    Optional<Min> min = context.findAnnotation(Min.class);
    Optional<Max> max = context.findAnnotation(Max.class);

    if (min.isPresent() || max.isPresent()) {
      Compatibility<AllowableRangeValues, NumericElementFacet> values = allowableRange(min, max);
      LOG.debug(
          "Adding allowable range: min({}) - max({}}",
          values.getLegacy().map(AllowableRangeValues::getMin)
                .orElse("<empty>"),
          values.getLegacy().map(AllowableRangeValues::getMax)
                .orElse("<empty>"));
      context.getParameterBuilder()
             .allowableValues(values.getLegacy().orElse(null));

      LOG.debug(
          "Adding numeric element facet: {}",
          values.getModern().map(NumericElementFacet::toString)
                .orElse("<empty>"));
      context.getRequestParameterBuilder()
             .query(q -> q.numericFacet(c -> c.copyOf(values.getModern().orElse(null))));
    }
  }

}
