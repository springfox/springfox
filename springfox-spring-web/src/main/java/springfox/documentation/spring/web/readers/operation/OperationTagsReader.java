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

package springfox.documentation.spring.web.readers.operation;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationTagsReader implements OperationBuilderPlugin {
  @Autowired
  private DocumentationPluginsManager pluginsManager;

  @Override
  public void apply(OperationContext context) {
    ResourceGroupingStrategy groupingStrategy
        = pluginsManager.resourceGroupingStrategy(context.getDocumentationType());
    Set<ResourceGroup> resourceGroups
            = groupingStrategy.getResourceGroups(context.getRequestMappingInfo(), context.getHandlerMethod());
    FluentIterable<String> tags = FluentIterable
            .from(resourceGroups)
            .transform(toTags());
    context.operationBuilder().tags(tags.toSet());  
  }

  private Function<ResourceGroup, String> toTags() {
    return new Function<ResourceGroup, String>() {
      @Override
      public String apply(ResourceGroup input) {
        return input.getGroupName(); 
      }
    };
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
