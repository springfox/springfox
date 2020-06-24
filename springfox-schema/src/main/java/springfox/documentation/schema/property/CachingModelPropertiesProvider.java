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
package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@SuppressWarnings("deprecation")
@Component
@Qualifier("cachedModelProperties")
public class CachingModelPropertiesProvider implements ModelPropertiesProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingModelPropertiesProvider.class);
  private final Map<ModelContext, List<springfox.documentation.schema.ModelProperty>> cache;
  private final Map<ModelContext, List<PropertySpecification>> specificationCache;
  private final Function<ModelContext, List<springfox.documentation.schema.ModelProperty>> lookup;
  private final Function<ModelContext, List<PropertySpecification>> lookupSpecification;

  @Autowired
  public CachingModelPropertiesProvider(
      final TypeResolver resolver,
      @Qualifier("optimized") final ModelPropertiesProvider delegate) {
    cache = new HashMap<>();
    specificationCache = new HashMap<>();
    lookup = (key) -> delegate.propertiesFor(key.resolvedType(resolver), key);
    lookupSpecification = (key) -> delegate.propertySpecificationsFor(key.resolvedType(resolver), key);
  }

  @Override
  public List<springfox.documentation.schema.ModelProperty>
  propertiesFor(ResolvedType type, ModelContext givenContext) {
    try {
      return cache.computeIfAbsent(givenContext, lookup);
    } catch (Exception e) {
      LOGGER.warn("Exception calculating properties for model({}) -> {}. {}",
          type, givenContext.description(), e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public List<PropertySpecification> propertySpecificationsFor(
      ResolvedType propertiesHost,
      ModelContext context) {
    try {
      return specificationCache.computeIfAbsent(context, lookupSpecification);
    } catch (Exception e) {
      LOGGER.warn("Exception calculating properties for model({}) -> {}. {}",
                  propertiesHost, context.description(), e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    //No-op
  }
}
