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

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.contexts.ModelContext;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("cachedModels")
public class CachingModelProvider implements ModelProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelProvider.class);
  private final LoadingCache<ModelContext, Map<ModelContext, Model>> cache;
  private final ModelProvider delegate;

  @Autowired
  public CachingModelProvider(@Qualifier("default") final ModelProvider delegate) {
    this.delegate = delegate;
    cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build(
            new CacheLoader<ModelContext, Map<ModelContext, Model>>() {
              public Map<ModelContext, Model> load(ModelContext key) {
                return delegate.modelsFor(key);
              }
            });
  }

  @Override
  public Map<ModelContext, Model> modelsFor(ModelContext modelContext) {
    try {
      return cache.get(modelContext);
    } catch (Exception e) {
      LOGGER.warn("Failed to get the model for -> {}. {}", modelContext.description(), e.getMessage());
      return newHashMap();
    }
  }
  /*
  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    return delegate.dependencies(modelContext);
  }*/
}
