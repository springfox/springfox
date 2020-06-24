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

package springfox.documentation.spring.web.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.CachingModelDependencyProvider
import springfox.documentation.schema.CachingModelProvider
import springfox.documentation.schema.DefaultModelDependencyProvider
import springfox.documentation.schema.DefaultModelProvider
import springfox.documentation.schema.DefaultModelSpecificationProvider
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.schema.property.CachingModelPropertiesProvider
import springfox.documentation.schema.property.FactoryMethodProvider
import springfox.documentation.schema.property.ModelSpecificationFactory
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.OptimizedModelPropertiesProvider
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spi.schema.TypeNameProviderPlugin

trait ModelProviderForServiceSupport implements ServicePluginsSupport {
  def typeNameExtractor() {
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
  }

  ModelProvider modelProvider(
      SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
      TypeResolver typeResolver = new TypeResolver(),
      EnumTypeDeterminer enumTypeDeterminer = new JacksonEnumTypeDeterminer(),
      ObjectMapper objectMapper = new ObjectMapper()) {

    def typeNameExtractor = typeNameExtractor()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()

    def event = new ObjectMapperConfigured(this, objectMapper)
    namingStrategy.onApplicationEvent(event)

    def modelPropertiesProvider = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        pluginsManager,
        enumTypeDeterminer,
        typeNameExtractor,
        new ModelSpecificationFactory(typeNameExtractor, enumTypeDeterminer))

    modelPropertiesProvider.onApplicationEvent(event)
    def modelDependenciesProvider =
        new DefaultModelDependencyProvider(
            typeResolver,
            new CachingModelPropertiesProvider(typeResolver, modelPropertiesProvider),
            typeNameExtractor,
            enumTypeDeterminer,
            defaultSchemaPlugins())
    new CachingModelProvider(
        new DefaultModelProvider(
            modelPropertiesProvider,
          new CachingModelDependencyProvider(modelDependenciesProvider),
          pluginsManager,
          typeNameExtractor,
          enumTypeDeterminer),
        new DefaultModelSpecificationProvider(
            typeResolver,
            modelPropertiesProvider,
            modelDependenciesProvider,
            pluginsManager,
            typeNameExtractor,
            enumTypeDeterminer,
            new ModelSpecificationFactory(
                typeNameExtractor,
                enumTypeDeterminer)))
  }

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(
      SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
      TypeResolver typeResolver = new TypeResolver()) {

    EnumTypeDeterminer enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()

    def event = new ObjectMapperConfigured(this, objectMapper)
    namingStrategy.onApplicationEvent(event)

    def modelPropertiesProvider = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        pluginsManager,
        enumTypeDeterminer,
        typeNameExtractor,
        new ModelSpecificationFactory(typeNameExtractor, enumTypeDeterminer))
    
    modelPropertiesProvider.onApplicationEvent(event)
    def modelDependenciesProvider =
        new DefaultModelDependencyProvider(
            typeResolver,
            modelPropertiesProvider,
            typeNameExtractor,
            enumTypeDeterminer,
            defaultSchemaPlugins())
    new DefaultModelProvider(
        modelPropertiesProvider,
        modelDependenciesProvider,
        pluginsManager,
        typeNameExtractor,
        enumTypeDeterminer)
  }
}
