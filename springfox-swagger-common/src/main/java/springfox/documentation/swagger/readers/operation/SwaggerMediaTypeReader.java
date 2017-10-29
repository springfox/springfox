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

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.util.Strings;


@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerMediaTypeReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    Optional<ApiOperation> annotation = context.findAnnotation(ApiOperation.class);
    if (annotation.isPresent()) {
      context.operationBuilder().consumes(asSet(Strings.nullToEmpty(annotation.get().consumes())));
      context.operationBuilder().produces(asSet(Strings.nullToEmpty(annotation.get().produces())));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }


  private Set<String> asSet(String mediaTypes) {
    String[] splitted = mediaTypes.split(",");
    return Arrays.stream(splitted)
        .map(String::trim)
        .filter(s -> s.length() > 0)
        .collect(Collectors.toSet());
  }

}
