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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("cachedModelProperties")
public class CachingModelPropertiesProvider implements ModelPropertiesProvider {

  private final LoadingCache<ModelContext, List<ModelProperty>> cache;

  @Autowired
  public CachingModelPropertiesProvider(
      final TypeResolver resolver,
      @Qualifier("optimized") final ModelPropertiesProvider delegate) {
    cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build(
            new CacheLoader<ModelContext, List<ModelProperty>>() {
              public List<ModelProperty> load(ModelContext key) {
                return delegate.propertiesFor(key.resolvedType(resolver), key);
              }
            });
  }

  @Override
  public List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext) {
    return cache.getUnchecked(givenContext);
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    //No-op
  }
}
