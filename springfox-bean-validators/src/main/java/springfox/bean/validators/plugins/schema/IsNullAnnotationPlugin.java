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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Null;
import java.util.Optional;

import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(BEAN_VALIDATOR_PLUGIN_ORDER)
public class IsNullAnnotationPlugin implements ModelPropertyBuilderPlugin {

  /**
   * support all documentationTypes
   */
  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  /**
   * read NotNull annotation
   */
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<Null> isNull = extractAnnotation(context);
    if (isNull.isPresent()) {
      context.getBuilder().readOnly(isNull.isPresent());
    }
  }

  private Optional<Null> extractAnnotation(ModelPropertyContext context) {
    return annotationFromBean(context, Null.class).map(Optional::of).orElse(annotationFromField(context, Null.class));
  }
}
