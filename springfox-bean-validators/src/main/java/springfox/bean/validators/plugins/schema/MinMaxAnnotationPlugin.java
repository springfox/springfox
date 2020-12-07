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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static springfox.bean.validators.plugins.RangeAnnotations.allowableRange;
import static springfox.bean.validators.plugins.Validators.extractAnnotation;

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
    Optional<Min> min = extractMinAnnotation(context);
    Optional<Max> max = extractMaxAnnotation(context);

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

  private Optional<Min> extractMinAnnotation(ModelPropertyContext context) {
    Set<Min> minSet = new HashSet<>();
    extractAnnotation(context, Min.class).ifPresent(minSet::add);
    extractAnnotation(context, Min.List.class).map(i -> Arrays.asList(i.value())).ifPresent(minSet::addAll);
    return minSet.stream().filter(min -> mustBeAppliedAccordingToValidatedGroups(context, min)).findAny();
  }

  private Optional<Max> extractMaxAnnotation(ModelPropertyContext context) {
    Set<Max> maxSet = new HashSet<>();
    extractAnnotation(context, Max.class).ifPresent(maxSet::add);
    extractAnnotation(context, Max.List.class).map(i -> Arrays.asList(i.value())).ifPresent(maxSet::addAll);
    return maxSet.stream().filter(max -> mustBeAppliedAccordingToValidatedGroups(context, max)).findAny();
  }

  public static boolean mustBeAppliedAccordingToValidatedGroups(ModelPropertyContext context, Max max) {
    return Validators.existsIntersectionBetweenGroupsFromValidatedAndConstraintAnnotations(context, max.groups());
  }

  public static boolean mustBeAppliedAccordingToValidatedGroups(ModelPropertyContext context, Min min) {
    return Validators.existsIntersectionBetweenGroupsFromValidatedAndConstraintAnnotations(context, min.groups());
  }
}
