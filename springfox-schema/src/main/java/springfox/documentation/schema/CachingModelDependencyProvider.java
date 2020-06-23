/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


@Component
@Qualifier("cachedModelDependencies")
public class CachingModelDependencyProvider implements ModelDependencyProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelDependencyProvider.class);
  private final Map<ModelContext, Set<ResolvedType>> cache;
  private final Function<ModelContext, Set<ResolvedType>> lookup;
  @Autowired
  public CachingModelDependencyProvider(@Qualifier("default") ModelDependencyProvider delegate) {
    cache = new HashMap<>();
    lookup = delegate::dependentModels;
  }

  @Override
  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    try {
      return cache.computeIfAbsent(modelContext, lookup);
    } catch (Exception e) {
      LOGGER.warn("Exception calculating dependencies for model -> {}, {}",
          modelContext.description(),
          e.getMessage()
      );
      return new HashSet<>();
    }
  }

}
