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


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static springfox.bean.validators.plugins.RangeAnnotations.stringLengthRange;
import static springfox.bean.validators.plugins.Validators.extractAnnotation;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class SizeAnnotationPlugin implements ModelPropertyBuilderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void apply(ModelPropertyContext context) {
    Optional<Size> size = extractSizeAnnotation(context);
    size.ifPresent(size1 -> {
      AllowableRangeValues allowableRangeValues = stringLengthRange(size1);
      context.getBuilder().allowableValues(allowableRangeValues);
      context.getSpecificationBuilder()
             .stringFacet(s -> {
               s.minLength(tryGetInteger(allowableRangeValues.getMin()).orElse(null));
               s.maxLength(tryGetInteger(allowableRangeValues.getMax()).orElse(null));
             });
    });
  }


  private Optional<Integer> tryGetInteger(String min) {
    try {
      return Optional.of(Integer.valueOf(min));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Optional<Size> extractSizeAnnotation(ModelPropertyContext context) {
    Set<Size> sizeSet = new HashSet<>();
    extractAnnotation(context, Size.class).ifPresent(sizeSet::add);
    extractAnnotation(context, Size.List.class).map(i -> Arrays.asList(i.value())).ifPresent(sizeSet::addAll);
    return sizeSet.stream().filter(size -> mustBeAppliedAccordingToValidatedGroups(context, size)).findAny();
  }

  public static boolean mustBeAppliedAccordingToValidatedGroups(ModelPropertyContext context, Size size) {
    return Validators.existsIntersectionBetweenGroupsFromValidatedAndConstraintAnnotations(context, size.groups());
  }
}
