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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import springfox.documentation.spi.service.DocumentationPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.FluentIterable.*;

class DuplicateGroupsDetector {
  private DuplicateGroupsDetector() {
    throw new UnsupportedOperationException();
  }

  public static void ensureNoDuplicateGroups(List<DocumentationPlugin> allPlugins) throws IllegalStateException {
    Multimap<String, DocumentationPlugin> plugins = Multimaps.index(allPlugins, byGroupName());
    Iterable<String> duplicateGroups = from(plugins.asMap().entrySet()).filter(duplicates()).transform(toGroupNames());
    if (Iterables.size(duplicateGroups) > 0) {
      throw new IllegalStateException(String.format("Multiple Dockets with the same group name are not supported. "
              + "The following duplicate groups were discovered. %s", Joiner.on(',').join(duplicateGroups)));
    }
  }

  private static Function<? super Map.Entry<String, Collection<DocumentationPlugin>>, String> toGroupNames() {
    return new Function<Map.Entry<String, Collection<DocumentationPlugin>>, String>() {
      @Override
      public String apply(Map.Entry<String, Collection<DocumentationPlugin>> input) {
        return input.getKey();
      }
    };
  }

  private static Predicate<? super Map.Entry<String, Collection<DocumentationPlugin>>> duplicates() {
    return new Predicate<Map.Entry<String, Collection<DocumentationPlugin>>>() {
      @Override
      public boolean apply(Map.Entry<String, Collection<DocumentationPlugin>> input) {
        return input.getValue().size() > 1;
      }
    };
  }


  private static Function<? super DocumentationPlugin, String> byGroupName() {
    return new Function<DocumentationPlugin, String>() {
      @Override
      public String apply(DocumentationPlugin input) {
        return Optional.fromNullable(input.getGroupName()).or("default");
      }
    };
  }
}
