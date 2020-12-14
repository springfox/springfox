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
package springfox.bean.validators.plugins.schema;


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static springfox.bean.validators.plugins.Validators.extractAnnotation;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class PatternAnnotationPlugin implements ModelPropertyBuilderPlugin {

  @Override
  @SuppressWarnings("deprecation")
  public void apply(ModelPropertyContext context) {
    Optional<Pattern> pattern = extractPatternAnnotation(context);
    String patternValueFromAnnotation = createPatternValueFromAnnotation(pattern);
    context.getBuilder().pattern(patternValueFromAnnotation);
    if (patternValueFromAnnotation != null) {
      context.getSpecificationBuilder()
             .stringFacet(s -> s.pattern(patternValueFromAnnotation));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private Optional<Pattern> extractPatternAnnotation(ModelPropertyContext context) {
    Set<Pattern> patternSet = new HashSet<>();
    extractAnnotation(context, Pattern.class).ifPresent(patternSet::add);
    extractAnnotation(context, Pattern.List.class).map(i -> Arrays.asList(i.value())).ifPresent(patternSet::addAll);
    return patternSet.stream().filter(pattern -> mustBeAppliedAccordingToValidatedGroups(context, pattern)).findAny();
  }

  private String createPatternValueFromAnnotation(Optional<Pattern> pattern) {
    String patternValue = null;
    if (pattern.isPresent()) {
      patternValue = pattern.get().regexp();
    }
    return patternValue;
  }

  private boolean mustBeAppliedAccordingToValidatedGroups(ModelPropertyContext context, Pattern pattern) {
    return Validators.annotationMustBeApplied(context, pattern.groups());
  }
}
