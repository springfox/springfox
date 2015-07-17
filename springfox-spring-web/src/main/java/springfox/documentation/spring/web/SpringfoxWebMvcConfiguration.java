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

package springfox.documentation.spring.web;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.schema.configuration.ModelsConfiguration;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.*;

@Configuration
@Import({ ModelsConfiguration.class })
@ComponentScan(basePackages = {
    "springfox.documentation.spring.web.scanners",
    "springfox.documentation.spring.web.readers.operation",
    "springfox.documentation.spring.web.readers.parameter",
    "springfox.documentation.spring.web.plugins",
    "springfox.documentation.spring.web.paths"
})
@EnablePluginRegistries({ DocumentationPlugin.class,
    ApiListingBuilderPlugin.class,
    OperationBuilderPlugin.class,
    ParameterBuilderPlugin.class,
    ExpandedParameterBuilderPlugin.class,
    ResourceGroupingStrategy.class,
    OperationModelsProviderPlugin.class,
    DefaultsProviderPlugin.class,
    PathDecorator.class
})
public class SpringfoxWebMvcConfiguration {

  @Autowired
  private Supplier<ArrayList<Cache>> modelCaches;

  @Bean
  public Defaults defaults() {
    return new Defaults();
  }

  @Bean
  public DocumentationCache resourceGroupCache() {
    return new DocumentationCache();
  }

  @Bean
  public static ObjectMapperConfigurer objectMapperConfigurer() {
    return new ObjectMapperConfigurer();
  }

  @Bean
  public JsonSerializer jsonSerializer(List<JacksonModuleRegistrar> moduleRegistrars) {
    return new JsonSerializer(moduleRegistrars);
  }

  @Bean
  public KeyGenerator operationsKeyGenerator() {
    return new OperationsKeyGenerator();
  }

  @Bean
  @Autowired
  public Supplier<? extends CacheManager> springfoxCacheManagerSupplier() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    List<Cache> caches = newArrayList();
    caches.addAll(modelCaches.get());
    caches.add(operationsCache());
    cacheManager.setCaches(caches);
    cacheManager.afterPropertiesSet();
    return Suppliers.ofInstance(cacheManager);
  }

  private Cache operationsCache() {
    return new ConcurrentMapCache("operations");
  }

}