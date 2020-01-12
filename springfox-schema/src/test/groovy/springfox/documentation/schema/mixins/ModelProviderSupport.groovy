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

package springfox.documentation.schema.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultModelDependencyProvider
import springfox.documentation.schema.DefaultModelProvider
import springfox.documentation.schema.DefaultModelSpecificationProvider
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.ModelSpecificationProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.property.FactoryMethodProvider
import springfox.documentation.schema.property.ModelPropertiesProvider
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.OptimizedModelPropertiesProvider
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spi.schema.TypeNameProviderPlugin

@SuppressWarnings("GrMethodMayBeStatic")
trait ModelProviderSupport extends SchemaPluginsSupport implements TypesForTestingSupport, AlternateTypesSupport {

  ModelProvider defaultModelProvider(
      ObjectMapper objectMapper = new ObjectMapper(),
      TypeResolver typeResolver = new TypeResolver(),
      EnumTypeDeterminer enumTypeDeterminer = new JacksonEnumTypeDeterminer()) {

    def pluginsManager = defaultSchemaPlugins()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
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
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor)

    modelPropertiesProvider.onApplicationEvent(event)
    def modelDependenciesProvider = modelDependencyProvider(
        typeResolver,
        modelPropertiesProvider,
        typeNameExtractor)

    new DefaultModelProvider(
        typeResolver,
        modelPropertiesProvider,
        modelDependenciesProvider,
        pluginsManager,
        typeNameExtractor,
        enumTypeDeterminer
    )
  }

  ModelSpecificationProvider defaultModelSpecificationProvider(
      ObjectMapper objectMapper = new ObjectMapper(),
      TypeResolver typeResolver = new TypeResolver(),
      EnumTypeDeterminer enumTypeDeterminer = new JacksonEnumTypeDeterminer()) {

    def pluginsManager = defaultSchemaPlugins()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
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
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor)

    modelPropertiesProvider.onApplicationEvent(event)
    def modelDependenciesProvider = modelDependencyProvider(
        typeResolver,
        modelPropertiesProvider,
        typeNameExtractor)

    new DefaultModelSpecificationProvider(
        typeResolver,
        modelPropertiesProvider,
        modelDependenciesProvider,
        pluginsManager,
        typeNameExtractor,
        enumTypeDeterminer
    )
  }

  DefaultModelDependencyProvider modelDependencyProvider(
      TypeResolver resolver,
      ModelPropertiesProvider modelPropertiesProvider,
      TypeNameExtractor typeNameExtractor) {

    new DefaultModelDependencyProvider(
        resolver,
        modelPropertiesProvider,
        typeNameExtractor,
        new JacksonEnumTypeDeterminer(),
        defaultSchemaPlugins())
  }

  DefaultModelDependencyProvider defaultModelDependencyProvider() {
    def typeResolver = new TypeResolver()
    def pluginsManager = defaultSchemaPlugins()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    def objectMapper = new ObjectMapper()
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
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor)
    modelPropertiesProvider.onApplicationEvent(event)
    modelDependencyProvider(typeResolver, modelPropertiesProvider, typeNameExtractor)
  }

}
