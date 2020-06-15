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

package springfox.documentation.swagger.readers.operation;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

import static springfox.documentation.builders.BuilderDefaults.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationNotesReader implements OperationBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public OperationNotesReader(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(OperationContext context) {

    Optional<ApiOperation> methodAnnotation = context.findAnnotation(ApiOperation.class);
    if (methodAnnotation.isPresent() && StringUtils.hasText(methodAnnotation.get().notes())) {
      context.operationBuilder()
             .notes(descriptions.resolve(methodAnnotation.get().notes()));
    }

    Optional<Operation> operationAnnotation = context.findAnnotation(Operation.class);
    operationAnnotation.ifPresent(a -> {
      if (StringUtils.hasText(a.description())) {
        context.operationBuilder()
               .notes(descriptions.resolve(a.description()));
      }
      context.operationBuilder().summary(emptyToNull(a.summary()));
    });
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
