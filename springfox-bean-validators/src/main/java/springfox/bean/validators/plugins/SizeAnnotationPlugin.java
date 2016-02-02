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

import com.google.common.annotations.VisibleForTesting;
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
    Optional<Size> size = extractAnnotation(context);

    if (size.isPresent()) {
      context.getBuilder().allowableValues(createAllowableValuesFromSizeForStrings(size.get()));
    }
  }

  @VisibleForTesting
  Optional<Size> extractAnnotation(ModelPropertyContext context) {
    return validatorFromBean(context, Size.class)
        .or(validatorFromField(context, Size.class));
  }

  private AllowableValues createAllowableValuesFromSizeForStrings(Size size) {
    AllowableRangeValues range = null;
    LOG.debug("@Size detected: adding MinLength/MaxLength to field");

    if (size.min() > 0 && size.max() < Integer.MAX_VALUE) {
      range = new AllowableRangeValues(Integer.toString(size.min()), Integer.toString(size.max()));
    } else if (size.min() > 0) {
      LOG.debug("@Size min detected: adding AllowableRangeValues to field");
      range = new AllowableRangeValues(Integer.toString(size.min()), Integer.toString(Integer.MAX_VALUE));
    } else if (size.max() < Integer.MAX_VALUE) {
      LOG.debug("@Size max detected: adding AllowableRangeValues to field");
      range = new AllowableRangeValues(Integer.toString(0), Integer.toString(size.max()));
    }
    return range;
  }


}
