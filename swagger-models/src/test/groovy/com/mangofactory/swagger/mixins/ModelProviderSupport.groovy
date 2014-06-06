package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider

class ModelProviderSupport {
  ModelProvider defaultModelProvider() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def accessors = new AccessorsProvider(resolver)
    def objectMapper = new ObjectMapper()
    def alternateTypeProvider = new AlternateTypeProvider()
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(objectMapper, accessors, fields)
    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  ModelProvider providerThatIgnoresHttpHeader() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def accessors = new AccessorsProvider(resolver)
    def objectMapper = new ObjectMapper()
    def alternateTypeProvider = new AlternateTypeProvider()
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(objectMapper, accessors, fields)
    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  private ModelDependencyProvider modelDependencyProvider(TypeResolver resolver,
    AlternateTypeProvider alternateTypeProvider, DefaultModelPropertiesProvider modelPropertiesProvider) {
    new ModelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
  }

  private ModelDependencyProvider defaultModelDependencyProvider() {
    def resolver = new TypeResolver()
    def objectMapper = new ObjectMapper()
    def fields = new FieldsProvider(resolver)
    def accessors = new AccessorsProvider(resolver)
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(objectMapper, accessors, fields)
    modelDependencyProvider(resolver, new AlternateTypeProvider(), modelPropertiesProvider)
  }

}
