/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
import springfox.bean.validators.util.MinMaxUtil;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ExpandedParameterMinMaxAnnotationPlugin implements ExpandedParameterBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterMinMaxAnnotationPlugin.class);

  public static <T extends Annotation> Optional<T> validatorFromBean(
      ParameterExpansionContext context,
      Class<T> annotationType) {

    return Optional.absent();
  }

  public static <T extends Annotation> Optional<T> validatorFromField(
      ParameterExpansionContext context,
      Class<T> annotationType) {

    Field field = context.getField().getRawMember();
    Optional<T> notNull = Optional.absent();
    if (field != null) {
      LOG.debug("Annotation size present for " + field.getName() + "!!");
      notNull = Optional.fromNullable(field.getAnnotation(annotationType));
    }

    return notNull;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ParameterExpansionContext context) {

    Field myfield = context.getField().getRawMember();
    LOG.debug("expandedparam.myfield: " + myfield.getName());

    Optional<Min> min = extractMin(context);
    Optional<Max> max = extractMax(context);

    if (min.isPresent() || max.isPresent()) {
      AllowableRangeValues values = MinMaxUtil.createAllowableValuesFromMinMaxForNumbers(min, max);
      LOG.debug("adding allowable Values MinMax: " + values.getMin() + " - " + values.getMax());
      context.getParameterBuilder().allowableValues(values);
    }

  }

  @VisibleForTesting
  Optional<Min> extractMin(ParameterExpansionContext context) {
    return validatorFromField(context, Min.class);
  }

  @VisibleForTesting
  Optional<Max> extractMax(ParameterExpansionContext context) {
    return validatorFromField(context, Max.class);
  }

  @VisibleForTesting
  Optional<Size> extractAnnotation(ParameterExpansionContext context) {

    return validatorFromBean(context, Size.class).or(validatorFromField(context, Size.class));
  }

}
