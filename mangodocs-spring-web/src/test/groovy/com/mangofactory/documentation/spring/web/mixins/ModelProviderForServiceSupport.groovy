package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.DefaultModelProvider
import com.mangofactory.documentation.schema.ModelDependencyProvider
import com.mangofactory.documentation.schema.ModelProvider
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.documentation.schema.property.bean.AccessorsProvider
import com.mangofactory.documentation.schema.property.bean.BeanModelPropertyProvider
import com.mangofactory.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldModelPropertyProvider
import com.mangofactory.documentation.schema.property.field.FieldProvider
import com.mangofactory.documentation.schema.property.provider.DefaultModelPropertiesProvider
import com.mangofactory.documentation.spi.service.contexts.Defaults

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([ServicePluginsSupport, SchemaPluginsSupport])
class ModelProviderForServiceSupport {
  def typeNameExtractor() {
    new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())
  }

  ModelProvider modelProvider(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
                              TypeResolver typeResolver = new TypeResolver()) {

    def defaults = new Defaults()
    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, namingStrategy, pluginsManager, typeNameExtractor)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
      TypeResolver typeResolver = new TypeResolver()) {

    def defaults = new Defaults()
    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, namingStrategy, pluginsManager,
                    typeNameExtractor)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }


}
