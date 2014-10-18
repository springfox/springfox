package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration

class SpringSwaggerConfigSupport {

  def SpringSwaggerConfig springSwaggerConfig() {
    SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
    springSwaggerConfig.setAlternateTypeProvider(new AlternateTypeProvider());
    def modelConfig = new SwaggerModelsConfiguration()
    def typeResolver = new TypeResolver()
    springSwaggerConfig.alternateTypeProvider = modelConfig.alternateTypeProvider()
    springSwaggerConfig.typeResolver = new TypeResolver()
    springSwaggerConfig
  }
}
