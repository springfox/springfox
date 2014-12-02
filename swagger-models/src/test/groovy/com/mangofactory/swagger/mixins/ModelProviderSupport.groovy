package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.ModelDependencyProvider
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.alternates.AlternateTypeRule
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.models.property.bean.AccessorsProvider
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldProvider
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity

@SuppressWarnings("GrMethodMayBeStatic")
class ModelProviderSupport {
  ModelProvider providerThatSubstitutesLocalDateWithString() {
    TypeResolver typeResolver = new TypeResolver()
    AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()
    alternateTypeProvider.addRule(new AlternateTypeRule(typeResolver.resolve(LocalDate), typeResolver.resolve(String)))
    defaultModelProvider(new ObjectMapper(), typeResolver, alternateTypeProvider)
  }

  ModelProvider providerThatSubstitutesResponseEntityOfVoid() {
    def resolver = new TypeResolver()
    def alternateTypeProvider = new AlternateTypeProvider()
    alternateTypeProvider.addRule(new AlternateTypeRule(resolver.resolve(ResponseEntity, Void),
            resolver.resolve(Void)))
    defaultModelProvider(new ObjectMapper(), resolver, alternateTypeProvider)
  }

  ModelProvider defaultModelProvider(ObjectMapper objectMapper = new ObjectMapper(),
                                     TypeResolver typeResolver = new  TypeResolver(),
                                     AlternateTypeProvider alternateTypeProvider = defaultAlternateTypesProvider()) {

    def fields = new FieldProvider(typeResolver)

    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)
    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = modelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  def defaultAlternateTypesProvider() {
    return new SwaggerModelsConfiguration().alternateTypeProvider(new TypeResolver())
  }

  private ModelDependencyProvider modelDependencyProvider(TypeResolver resolver,
      AlternateTypeProvider alternateTypeProvider, DefaultModelPropertiesProvider modelPropertiesProvider) {
    new ModelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
  }

  ModelDependencyProvider defaultModelDependencyProvider() {
    def typeResolver = new TypeResolver()
    def fields = new FieldProvider(typeResolver)
    def alternateTypeProvider = new AlternateTypeProvider()

    def objectMapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper);

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)
    modelPropertiesProvider.objectMapper = objectMapper
    modelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
  }

}
