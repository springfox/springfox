/*
 *
 *  Copyright 2017-2019 the original author or authors.
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


import com.google.common.base.Predicate;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;


import static java.util.stream.Collectors.toList;
import static springfox.documentation.spi.service.contexts.Orderings.*;

class ResourceGroups {
  private ResourceGroups() {
    throw new UnsupportedOperationException();
  }

  static Iterable<ResourceGroup> collectResourceGroups(Collection<ApiDescription> apiDescriptions) {
    return apiDescriptions.stream()
        .map(toResourceGroups()).collect(toList());
  }

  static Iterable<ResourceGroup> sortedByName(Set<ResourceGroup> resourceGroups) {
    return resourceGroups.stream().sorted(resourceGroupComparator()).collect(toList());
  }

  static Predicate<ApiDescription> belongsTo(final String groupName) {
    return new Predicate<ApiDescription>() {
      @Override
      public boolean apply(ApiDescription input) {
        return !input.getGroupName().isPresent()
            || groupName.equals(input.getGroupName().get());
      }
    };
  }

  private static Function<ApiDescription, ResourceGroup> toResourceGroups() {
    return new Function<ApiDescription, ResourceGroup>() {
      @Override
      public ResourceGroup apply(ApiDescription input) {
        return new ResourceGroup(
            input.getGroupName().orElse(Docket.DEFAULT_GROUP_NAME),
            null);
      }
    };
  }
}
