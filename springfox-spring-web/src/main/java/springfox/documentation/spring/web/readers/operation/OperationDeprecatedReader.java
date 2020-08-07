/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.spring.web.readers.operation;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.Optional;

import static org.springframework.core.KotlinDetector.isKotlinPresent;
import static springfox.documentation.spring.kotlin.OperationContextHelper.isControllerDeprecated;
import static springfox.documentation.spring.kotlin.OperationContextHelper.isMethodDeprecated;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationDeprecatedReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    Optional<Deprecated> annotationOnMethod = context.findAnnotation(Deprecated.class);
    Optional<Deprecated> annotationOnController = context.findControllerAnnotation(Deprecated.class);

    boolean deprecated = annotationOnMethod.isPresent() || annotationOnController.isPresent();
    boolean kotlinDeprecated = isKotlinPresent() && (isMethodDeprecated(context) || isControllerDeprecated(context));

    context.operationBuilder().deprecated(deprecated || kotlinDeprecated ? "true" : null);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
