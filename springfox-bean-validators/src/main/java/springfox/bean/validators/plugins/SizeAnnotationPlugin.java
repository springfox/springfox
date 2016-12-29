/*
 *
 *  Copyright 2017 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Size;

import static springfox.bean.validators.plugins.BeanValidators.*;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class SizeAnnotationPlugin implements ModelPropertyBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(SizeAnnotationPlugin.class);

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Size> size = extractAnnotation(context, Size.class);

    if (size.isPresent()) {
      context.getBuilder().allowableValues(createAllowableValuesFromSizeForStrings(size.get()));
    }
  }

  private AllowableValues createAllowableValuesFromSizeForStrings(Size size) {
    LOG.debug("@Size detected: adding MinLength/MaxLength to field");
    return new AllowableRangeValues(minValue(size), maxValue(size));
  }

  private String minValue(Size size) {
    return String.valueOf(Math.max(size.min(), 0));
  }

  private String maxValue(Size size) {
    return String.valueOf(Math.max(0, Math.min(size.max(), Integer.MAX_VALUE)));
  }

}
