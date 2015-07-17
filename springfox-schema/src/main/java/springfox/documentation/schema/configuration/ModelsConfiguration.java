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

package springfox.documentation.schema.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;

import java.util.ArrayList;

import static com.google.common.collect.Lists.*;

@Configuration
@ComponentScan(basePackages = {
    "springfox.documentation.schema"
})
@EnablePluginRegistries({
    ModelBuilderPlugin.class,
    ModelPropertyBuilderPlugin.class,
    TypeNameProviderPlugin.class
})
public class ModelsConfiguration {
  @Bean
  public TypeResolver typeResolver() {
    return new TypeResolver();
  }

  @Bean
  Supplier<ArrayList<Cache>> modelCachesSupplier() {
    return Suppliers.ofInstance(newArrayList(modelsCache(), modelPropertiesCache(), modelDependenciesCache()));
  }

  private Cache modelsCache() {
    return new ConcurrentMapCache("models");
  }

  private Cache modelDependenciesCache() {
    return new ConcurrentMapCache("modelDependencies");
  }

  private Cache modelPropertiesCache() {
    return new ConcurrentMapCache("modelProperties");
  }
}
