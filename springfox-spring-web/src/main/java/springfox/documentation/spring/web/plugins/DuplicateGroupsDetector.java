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
package springfox.documentation.spring.web.plugins;


import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import springfox.documentation.spi.service.DocumentationPlugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


class DuplicateGroupsDetector {
  private DuplicateGroupsDetector() {
    throw new UnsupportedOperationException();
  }

  public static void ensureNoDuplicateGroups(List<DocumentationPlugin> allPlugins) throws IllegalStateException {
    Map<String, List<DocumentationPlugin>> plugins = allPlugins.stream().collect(groupingBy(byGroupName()));
    Iterable<String> duplicateGroups = plugins.entrySet().stream().filter(duplicates()).map(toGroupNames()).collect(toList());
    if (Iterables.size(duplicateGroups) > 0) {
      throw new IllegalStateException(String.format("Multiple Dockets with the same group name are not supported. "
              + "The following duplicate groups were discovered. %s", Joiner.on(',').join(duplicateGroups)));
    }
  }

  private static Function<? super Map.Entry<String, List<DocumentationPlugin>>, String> toGroupNames() {
    return new Function<Map.Entry<String, List<DocumentationPlugin>>, String>() {
      @Override
      public String apply(Map.Entry<String, List<DocumentationPlugin>> input) {
        return input.getKey();
      }
    };
  }

  private static java.util.function.Predicate<? super Map.Entry<String, List<DocumentationPlugin>>> duplicates() {
    return new java.util.function.Predicate<Map.Entry<String, List<DocumentationPlugin>>>() {
      @Override
      public boolean test(Map.Entry<String, List<DocumentationPlugin>> input) {
        return input.getValue().size() > 1;
      }
    };
  }


  private static Function<? super DocumentationPlugin, String> byGroupName() {
    return new Function<DocumentationPlugin, String>() {
      @Override
      public String apply(DocumentationPlugin input) {
        return Optional.ofNullable(input.getGroupName()).orElse("default");
      }
    };
  }
}
