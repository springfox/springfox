package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.ModelDependencyProvider
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.property.bean.AccessorsProvider
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldProvider
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider

@SuppressWarnings("GrMethodMayBeStatic")
class ModelProviderSupport {

  ModelProvider modelProvider(TypeResolver typeResolver = new TypeResolver(),
                              AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()) {

    def fields = new FieldProvider(typeResolver)

    def objectMapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(TypeResolver typeResolver = new TypeResolver(),
                              AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()) {

    def fields = new FieldProvider(typeResolver)

    def objectMapper = new ObjectMapper()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }


}
