package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.controllers.Defaults

import javax.servlet.ServletContext

@Mixin([ModelProviderSupport, MapperSupport])
@SuppressWarnings("GrMethodMayBeStatic")
class SpringSwaggerConfigSupport {

  def SpringSwaggerConfig springSwaggerConfig() {
    new SpringSwaggerConfig()
  }

  def Defaults defaults(ServletContext servletContext) {
    def typeResolver = new TypeResolver()
    def modelConfig = new SwaggerModelsConfiguration()
    def alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
    def modelProvider = modelProvider(typeResolver, modelConfig.alternateTypeProvider(typeResolver))
    new Defaults(servletContext, typeResolver, alternateTypeProvider, modelProvider)
  }
}
