package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration

@Mixin(ModelProviderSupport)
class SpringSwaggerConfigSupport {

  def SpringSwaggerConfig springSwaggerConfig() {
    SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
    def modelConfig = new SwaggerModelsConfiguration()
    def typeResolver = new TypeResolver()
    springSwaggerConfig.alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
    springSwaggerConfig.typeResolver = new TypeResolver()
    springSwaggerConfig.modelProvider = modelProvider(typeResolver, modelConfig.alternateTypeProvider(typeResolver))
    springSwaggerConfig
  }
}
