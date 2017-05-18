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

import com.google.common.base.Function;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;

import static springfox.documentation.spring.web.paths.Paths.*;

@Component
@Order(value= Ordered.HIGHEST_PRECEDENCE)
public class ApiListingReader implements ApiListingBuilderPlugin {
  @Override
  public void apply(ApiListingContext apiListingContext) {
    ResourceGroup group = apiListingContext.getResourceGroup();
    String description = group.getControllerClass()
        .transform(description())
        .or(group.getGroupName());

    apiListingContext.apiListingBuilder()
        .description(description);
  }

  private Function<Class<?>, String> description() {
    return new Function<Class<?>, String>() {
      @Override
      public String apply(Class<?> input) {
        return splitCamelCase(input.getSimpleName(), " ");
      }
    };
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
