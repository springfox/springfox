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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import springfox.documentation.spi.service.DocumentationPlugin;


class DuplicateGroupsDetector {
  private DuplicateGroupsDetector() {
    throw new UnsupportedOperationException();
  }

  public static void ensureNoDuplicateGroups(List<DocumentationPlugin> allPlugins) throws IllegalStateException {
    Map<String, List<DocumentationPlugin>> plugins = new HashMap<>();
    for (DocumentationPlugin documentationPlugin : allPlugins) {
      plugins.computeIfAbsent(Optional.ofNullable(documentationPlugin.getGroupName()).orElse("default"),
          k -> new ArrayList<>()).add(documentationPlugin);
    }
    List<String> duplicateGroups = plugins.entrySet().stream().filter(duplicates()).map(toGroupNames())
        .collect(Collectors.toList());
    if (duplicateGroups.size() > 0) {
      throw new IllegalStateException(String.format(
          "Multiple Dockets with the same group name are not supported. "
              + "The following duplicate groups were discovered. %s",
          duplicateGroups.stream().collect(Collectors.joining(","))));
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

  private static Predicate<? super Map.Entry<String, List<DocumentationPlugin>>> duplicates() {
    return new Predicate<Map.Entry<String, List<DocumentationPlugin>>>() {
      @Override
      public boolean test(Map.Entry<String, List<DocumentationPlugin>> input) {
        return input.getValue().size() > 1;
      }
    };
  }

}
