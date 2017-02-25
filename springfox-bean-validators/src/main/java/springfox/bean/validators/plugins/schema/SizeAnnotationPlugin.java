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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Size;

import static springfox.bean.validators.plugins.RangeAnnotations.*;
import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class SizeAnnotationPlugin implements ModelPropertyBuilderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Size> size = extractAnnotation(context);

    if (size.isPresent()) {
      context.getBuilder().allowableValues(stringLengthRange(size.get()));
    }
  }

  @VisibleForTesting
  Optional<Size> extractAnnotation(ModelPropertyContext context) {
    return annotationFromBean(context, Size.class).or(annotationFromField(context, Size.class));
  }
}
