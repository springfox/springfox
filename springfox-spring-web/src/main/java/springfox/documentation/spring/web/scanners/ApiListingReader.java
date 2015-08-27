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
package springfox.documentation.spring.web.scanners;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;

import static springfox.documentation.spring.web.paths.Paths.*;

@Component
@Order(value= Ordered.HIGHEST_PRECEDENCE)
public class ApiListingReader implements ApiListingBuilderPlugin {
  @Override
  public void apply(ApiListingContext apiListingContext) {
    Class<?> controllerClass = apiListingContext.getResourceGroup().getControllerClass();
    String description = splitCamelCase(controllerClass.getSimpleName(), " ");

    apiListingContext.apiListingBuilder()
        .description(description);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
