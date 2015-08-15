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
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.Set;

@Component
public class DefaultTagsProvider {
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public DefaultTagsProvider(DocumentationPluginsManager pluginsManager) {
    this.pluginsManager = pluginsManager;
  }

  public ImmutableSet<String> tags(OperationContext context) {
    ResourceGroupingStrategy groupingStrategy
        = pluginsManager.resourceGroupingStrategy(context.getDocumentationType());
    Set<ResourceGroup> resourceGroups
        = groupingStrategy.getResourceGroups(context.getRequestMappingInfo(), context.getHandlerMethod());
    FluentIterable<String> tags = FluentIterable
        .from(resourceGroups)
        .transform(toTags());
    return tags.toSet();
  }

  private Function<ResourceGroup, String> toTags() {
    return new Function<ResourceGroup, String>() {
      @Override
      public String apply(ResourceGroup input) {
        return input.getGroupName();
      }
    };
  }
}
