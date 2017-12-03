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
package springfox.documentation.schema.property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spi.schema.contexts.ModelContext;

@Component
@Qualifier("cachedModelProperties")
public class CachingModelPropertiesProvider implements ModelPropertiesProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelPropertiesProvider.class);
  //private final LoadingCache<ModelContext, List<ModelProperty>> cache;
  private final Map<ModelContext, List<ModelProperty>> cache = new LinkedHashMap<ModelContext, List<ModelProperty>>() {
    protected boolean removeEldestEntry(Map.Entry<ModelContext, List<ModelProperty>> eldest) {
      return size() > 1000;
    }
  };
  private final TypeResolver resolver;
  private final ModelPropertiesProvider delegate;

  @Autowired
  public CachingModelPropertiesProvider(final TypeResolver resolver,
      @Qualifier("optimized") final ModelPropertiesProvider delegate) {
    // cache = CacheBuilder.newBuilder()
    // .maximumSize(1000)
    // .expireAfterWrite(24, TimeUnit.HOURS)
    // .build(
    // new CacheLoader<ModelContext, List<ModelProperty>>() {
    // public List<ModelProperty> load(ModelContext key) {
    // return delegate.propertiesFor(key.resolvedType(resolver), key);
    // }
    // });
    this.resolver = resolver;
    this.delegate = delegate;
  }

  @Override
  public List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext) {
    try {
      return cache.computeIfAbsent(givenContext, key -> delegate.propertiesFor(key.resolvedType(resolver), key));
    } catch (Exception e) {
      LOGGER.warn("Exception calculating properties for model({}) -> {}. {}",
          type, givenContext.description(), e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    //No-op
  }
}
