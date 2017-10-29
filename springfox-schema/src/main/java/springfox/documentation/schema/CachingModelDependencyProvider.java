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


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;

import springfox.documentation.spi.schema.contexts.ModelContext;

@Component
@Qualifier("cachedModelDependencies")
public class CachingModelDependencyProvider implements ModelDependencyProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelDependencyProvider.class);
  //private final LoadingCache<ModelContext, Set<ResolvedType>> cache;
  private final Map<ModelContext, Set<ResolvedType>> cache = new LinkedHashMap<ModelContext, Set<ResolvedType>>() {
    protected boolean removeEldestEntry(Map.Entry<ModelContext, Set<ResolvedType>> eldest) {
      return size() > 1000;
    }
  };
  private final ModelDependencyProvider modelDependencyProvider;

  @Autowired
  public CachingModelDependencyProvider(@Qualifier("default") final ModelDependencyProvider delegate) {
    // cache = CacheBuilder.newBuilder()
    // .maximumSize(1000)
    // .expireAfterWrite(24, TimeUnit.HOURS)
    // .build(new CacheLoader<ModelContext, Set<ResolvedType>>() {
    // public Set<ResolvedType> load(ModelContext key) {
    // return delegate.dependentModels(key);
    // }
    // });
    this.modelDependencyProvider = delegate;
  }

  @Override
  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    try {
      return cache.computeIfAbsent(modelContext, key -> modelDependencyProvider.dependentModels(key));
    } catch (Exception e) {
      LOGGER.warn("Exception calculating dependencies for model -> {}, {}",
          modelContext.description(),
          e.getMessage()
      );
      return new HashSet<>();
    }
  }

}
