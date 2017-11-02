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
package springfox.documentation.schema;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.schema.contexts.ModelContext;

@Component
@Qualifier("cachedModels")
public class CachingModelProvider implements ModelProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelProvider.class);
  //private final LoadingCache<ModelContext, Optional<Model>> cache;
  private final Map<ModelContext, Optional<Model>> cache = new LinkedHashMap<ModelContext, Optional<Model>>() {
    protected boolean removeEldestEntry(Map.Entry<ModelContext, Optional<Model>> eldest) {
      return size() > 1000;
    }
  };
  private final ModelProvider delegate;

  @Autowired
  public CachingModelProvider(@Qualifier("default") final ModelProvider delegate) {
    this.delegate = delegate;
//    cache = CacheBuilder.newBuilder()
//        .maximumSize(1000)
//        .expireAfterWrite(24, TimeUnit.HOURS)
//        .build(
//            new CacheLoader<ModelContext, Optional<Model>>() {
//              public Optional<Model> load(ModelContext key) {
//                return delegate.modelFor(key);
//              }
//            });
  }

  @Override
  public Optional<Model> modelFor(ModelContext modelContext) {
    try {
      return cache.computeIfAbsent(modelContext, key -> delegate.modelFor(key));
    } catch (Exception e) {
      LOGGER.warn("Failed to get the model for -> {}. {}", modelContext.description(), e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    return delegate.dependencies(modelContext);
  }
}
