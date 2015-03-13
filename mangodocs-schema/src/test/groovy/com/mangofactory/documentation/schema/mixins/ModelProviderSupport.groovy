package com.mangofactory.documentation.schema.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.DefaultModelProvider
import com.mangofactory.documentation.schema.ModelDependencyProvider
import com.mangofactory.documentation.schema.ModelProvider
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.configuration.ObjectMapperConfigured
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.documentation.schema.property.bean.AccessorsProvider
import com.mangofactory.documentation.schema.property.bean.BeanModelPropertyProvider
import com.mangofactory.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldProvider
import com.mangofactory.documentation.schema.property.provider.DefaultModelPropertiesProvider

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([SchemaPluginsSupport])
class ModelProviderSupport {

  ModelProvider defaultModelProvider(ObjectMapper objectMapper = new ObjectMapper(),
                                     TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)

    def pluginsManager = defaultSchemaPlugins()
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver, new DefaultGenericTypeNamingStrategy(), pluginsManager)
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

  def beanProperty(TypeResolver typeResolver, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def fieldProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def constructorProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
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
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver, new DefaultGenericTypeNamingStrategy(),
            pluginsManager)
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
