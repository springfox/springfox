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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static springfox.bean.validators.plugins.RangeAnnotations.allowableRange;

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
    allowableRange(
        context.findAnnotation(Min.class),
        context.findAnnotation(Positive.class),
        context.findAnnotation(PositiveOrZero.class),
        context.findAnnotation(Max.class),
        context.findAnnotation(Negative.class),
        context.findAnnotation(NegativeOrZero.class))
        .ifPresent(values -> {
      LOG.debug("Adding allowable range: min({}) - max({}", values.getMin(), values.getMax());
      context.getParameterBuilder().allowableValues(values);
        });
  }
}
