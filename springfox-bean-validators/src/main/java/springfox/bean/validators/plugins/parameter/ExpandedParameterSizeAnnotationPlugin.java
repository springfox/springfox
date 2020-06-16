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
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Optional;

import static springfox.bean.validators.plugins.RangeAnnotations.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ExpandedParameterSizeAnnotationPlugin implements ExpandedParameterBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterSizeAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ParameterExpansionContext context) {

    Optional<Size> size = context.findAnnotation(Size.class);

    if (size.isPresent()) {
      AllowableRangeValues values = stringLengthRange(size.get());
      LOG.debug("Adding allowable Values: {} - {}", values.getMin(), values.getMax());

      values = new AllowableRangeValues(values.getMin(), values.getMax());
      context.getParameterBuilder().allowableValues(values);
      AllowableRangeValues finalValues = values;
      context.getRequestParameterBuilder()
             .query(q -> {
               q.numericFacet(n -> {
                 n.minimum(safeBigDecimal(finalValues.getMin()));
                 n.maximum(safeBigDecimal(finalValues.getMax()));
               });
               q.stringFacet(s -> {
                 s.minLength(safeBigDecimal(finalValues.getMin()).intValue());
                 s.maxLength(safeBigDecimal(finalValues.getMax()).intValue());
               });
             });
    }
  }

  static BigDecimal safeBigDecimal(String doubleString) {
    if (doubleString == null) {
      return null;
    }
    try {
      return new BigDecimal(doubleString);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
