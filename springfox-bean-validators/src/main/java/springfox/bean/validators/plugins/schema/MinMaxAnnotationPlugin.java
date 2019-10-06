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


import static springfox.bean.validators.plugins.RangeAnnotations.allowableRange;
import static springfox.bean.validators.plugins.Validators.annotationFromBean;
import static springfox.bean.validators.plugins.Validators.annotationFromField;

import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class MinMaxAnnotationPlugin implements ModelPropertyBuilderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Min> min = extractMin(context);
    Optional<Positive> positive = extractPositive(context);
    Optional<PositiveOrZero> positiveOrZero = extractPositiveOrZero(context);
    Optional<Max> max = extractMax(context);
    Optional<Negative> negative = extractNegative(context);
    Optional<NegativeOrZero> negativeOrZero = extractNegativeOrZero(context);

    allowableRange(min, positive, positiveOrZero, max, negative, negativeOrZero)
        .ifPresent(allowableRangeValues -> context.getBuilder().allowableValues(allowableRangeValues));
  }

  private Optional<Min> extractMin(ModelPropertyContext context) {
    return annotationFromBean(context, Min.class).map(Optional::of).orElse(annotationFromField(context, Min.class));
  }

  private Optional<Positive> extractPositive(ModelPropertyContext context) {
    return annotationFromBean(context, Positive.class).map(Optional::of)
        .orElse(annotationFromField(context, Positive.class));
  }

  private Optional<PositiveOrZero> extractPositiveOrZero(ModelPropertyContext context) {
    return annotationFromBean(context, PositiveOrZero.class).map(Optional::of)
        .orElse(annotationFromField(context, PositiveOrZero.class));
  }

  private Optional<Max> extractMax(ModelPropertyContext context) {
    return annotationFromBean(context, Max.class).map(Optional::of).orElse(annotationFromField(context, Max.class));
  }

  private Optional<Negative> extractNegative(ModelPropertyContext context) {
    return annotationFromBean(context, Negative.class).map(Optional::of)
        .orElse(annotationFromField(context, Negative.class));
  }

  private Optional<NegativeOrZero> extractNegativeOrZero(ModelPropertyContext context) {
    return annotationFromBean(context, NegativeOrZero.class).map(Optional::of)
        .orElse(annotationFromField(context, NegativeOrZero.class));
  }
}
