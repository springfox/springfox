package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.AccessorsProvider
import com.mangofactory.swagger.models.DefaultModelPropertiesProvider
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.FieldsProvider
import com.mangofactory.swagger.models.ModelDependencyProvider

class ModelProviderSupport {
  static def defaultModelProvider() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def accessors = new AccessorsProvider(resolver)
    def objectMapper = new ObjectMapper()
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(objectMapper, accessors, fields)
    def modelDependenciesProvider = modelDependencyProvider(resolver, modelPropertiesProvider)
    new DefaultModelProvider(resolver, modelPropertiesProvider, modelDependenciesProvider)
  }

  private static def modelDependencyProvider(TypeResolver resolver,
                                             DefaultModelPropertiesProvider modelPropertiesProvider) {
    new ModelDependencyProvider(resolver, modelPropertiesProvider)
  }

}
