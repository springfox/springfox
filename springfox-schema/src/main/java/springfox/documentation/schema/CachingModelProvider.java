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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;

@Component
@Qualifier("cachedModels")
public class CachingModelProvider implements ModelProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelProvider.class);
  private final Map<ModelContext, Optional<Model>> cache;
  private final Function<ModelContext, Optional<Model>> lookup;
  private final ModelProvider delegate;

  @Autowired
  public CachingModelProvider(@Qualifier("default") final ModelProvider delegate) {
    this.delegate = delegate;
    cache = new HashMap<>();
    lookup = delegate::modelFor;
  }

  @Override
  public Optional<Model> modelFor(ModelContext modelContext) {
    try {
      return cache.computeIfAbsent(modelContext, lookup);
    } catch (Exception e) {
      LOGGER.warn("Failed to get the model for -> {}. {}", modelContext.description(), e.getMessage());
      return empty();
    }
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    return delegate.dependencies(modelContext);
  }
}
