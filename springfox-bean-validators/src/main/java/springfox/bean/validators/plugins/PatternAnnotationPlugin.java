/*
 *
 *  Copyright 2016 the original author or authors.
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

import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Pattern;

import static springfox.bean.validators.plugins.BeanValidators.validatorFromBean;
import static springfox.bean.validators.plugins.BeanValidators.validatorFromField;

/**
 * @author : ashutosh
 *         28/04/2016
 */
@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class PatternAnnotationPlugin implements ModelPropertyBuilderPlugin {
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Pattern> pattern = extractPattern(context);
    context.getBuilder().pattern(createPatternValueFromAnnotation(pattern));
  }

  Optional<Pattern> extractPattern(ModelPropertyContext context) {
    return validatorFromBean(context, Pattern.class)
        .or(validatorFromField(context, Pattern.class));
  }

  private String createPatternValueFromAnnotation(Optional<Pattern> pattern) {
    String patternValue = null;
    if(pattern.isPresent()){
      patternValue = pattern.get().regexp();
    }
    return patternValue;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
