package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration

class ModelProviderSupport {
  def modelProvider() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
    def alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver());
    def accessors = new AccessorsProvider(resolver, alternateTypeProvider)
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(new ObjectMapper(), alternateTypeProvider,
            accessors, fields)
    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)

    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  private def modelDependencyProvider(TypeResolver resolver, AlternateTypeProvider alternateTypeProvider,
      DefaultModelPropertiesProvider modelPropertiesProvider) {
    new ModelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
  }

}
