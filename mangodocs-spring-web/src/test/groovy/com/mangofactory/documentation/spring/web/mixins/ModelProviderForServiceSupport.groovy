package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.DefaultModelProvider
import com.mangofactory.documentation.schema.ModelDependencyProvider
import com.mangofactory.documentation.schema.ModelProvider
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.configuration.ObjectMapperConfigured
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.documentation.schema.property.bean.AccessorsProvider
import com.mangofactory.documentation.schema.property.bean.BeanModelPropertyProvider
import com.mangofactory.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldProvider
import com.mangofactory.documentation.schema.property.provider.DefaultModelPropertiesProvider

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([ServicePluginsSupport, SchemaPluginsSupport])
class ModelProviderForServiceSupport {
  def typeNameExtractor() {
    new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())
  }

  ModelProvider modelProvider(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
                              TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
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

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
      TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }


}
