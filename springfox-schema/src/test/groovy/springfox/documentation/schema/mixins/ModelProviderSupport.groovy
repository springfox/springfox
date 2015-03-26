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
import springfox.documentation.schema.DefaultModelProvider
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.bean.BeanModelPropertyProvider
import springfox.documentation.schema.property.provider.DefaultModelPropertiesProvider
import springfox.documentation.schema.ModelDependencyProvider
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import springfox.documentation.schema.property.field.FieldModelPropertyProvider
import springfox.documentation.schema.property.field.FieldProvider

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([SchemaPluginsSupport])
class ModelProviderSupport {

  ModelProvider defaultModelProvider(ObjectMapper objectMapper = new ObjectMapper(),
                                     TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)

    def pluginsManager = defaultSchemaPlugins()
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver, pluginsManager)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = modelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }

  def beanProperty(TypeResolver typeResolver, ObjectMapperBeanPropertyNamingStrategy namingStrategy,
                   SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def fieldProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy,
                    SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def constructorProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy,
                          SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, namingStrategy, pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  ModelDependencyProvider modelDependencyProvider(TypeResolver resolver,
      DefaultModelPropertiesProvider modelPropertiesProvider, 
      TypeNameExtractor typeNameExtractor) {
    new ModelDependencyProvider(resolver, modelPropertiesProvider, typeNameExtractor)
  }

  ModelDependencyProvider defaultModelDependencyProvider() {
    def typeResolver = new TypeResolver()
    def fields = new FieldProvider(typeResolver)

    def pluginsManager = defaultSchemaPlugins()
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver,  pluginsManager)
    def objectMapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    modelDependencyProvider(typeResolver, modelPropertiesProvider, typeNameExtractor)
  }

}
