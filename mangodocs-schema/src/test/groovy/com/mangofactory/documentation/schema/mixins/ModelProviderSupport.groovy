package com.mangofactory.documentation.schema.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.DefaultModelProvider
import com.mangofactory.documentation.schema.ModelDependencyProvider
import com.mangofactory.documentation.schema.ModelProvider
import com.mangofactory.documentation.schema.TypeNameExtractor
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
    def modelDependenciesProvider = modelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
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
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper);

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, namingStrategy, pluginsManager, typeNameExtractor)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)
    modelPropertiesProvider.objectMapper = objectMapper
    modelDependencyProvider(typeResolver, modelPropertiesProvider, typeNameExtractor)
  }

}
