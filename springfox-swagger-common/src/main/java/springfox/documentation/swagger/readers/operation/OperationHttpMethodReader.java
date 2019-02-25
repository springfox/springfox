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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationHttpMethodReader implements OperationBuilderPlugin {
  private static final Logger LOGGER = LoggerFactory.getLogger(OperationHttpMethodReader.class);

  @Override
  public void apply(OperationContext context) {

    Optional<ApiOperation> apiOperationAnnotation = context.findAnnotation(ApiOperation.class);

    if (apiOperationAnnotation.isPresent() && StringUtils.hasText(apiOperationAnnotation.get().httpMethod())) {
      String apiMethod = apiOperationAnnotation.get().httpMethod();
      try {
        RequestMethod.valueOf(apiMethod);
        context.operationBuilder().method(HttpMethod.valueOf(apiMethod));
      } catch (IllegalArgumentException e) {
        LOGGER.error("Invalid http method: " + apiMethod + "Valid ones are [" + RequestMethod.values() + "]", e);
      }
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
