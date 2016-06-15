/*
 *
 *  Copyright 2015 the original author or authors.
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
import java.lang.annotation.Annotation;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import springfox.bean.validators.util.SizeUtil;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import static springfox.bean.validators.plugins.BeanValidators.validatorFromParameterField;
@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ParameterSizeAnnotationPlugin implements ParameterBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ParameterSizeAnnotationPlugin.class);

  /**
   * support all documentationTypes
   */
  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  /** 
   * read Size annotation
   */
  @Override
  public void apply(ParameterContext context) {
    Optional<Size> size = extractAnnotation(context);
    
    if (size.isPresent()) {
        AllowableRangeValues values = SizeUtil.createAllowableValuesFromSizeForStrings(size.get());
        LOG.debug("adding allowable Values @Size: " + values.getMin() + " - " + values.getMax());
        context.parameterBuilder().allowableValues(values);
        
    }
  }

  @VisibleForTesting
  Optional<Size> extractAnnotation(ParameterContext context) {
    return validatorFromParameterField(context, Size.class);
  }
  


}
