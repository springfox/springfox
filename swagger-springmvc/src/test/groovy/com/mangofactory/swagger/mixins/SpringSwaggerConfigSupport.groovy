package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.spring.web.plugins.Defaults

import javax.servlet.ServletContext

@Mixin([ModelProviderForServiceSupport, MapperSupport])
@SuppressWarnings("GrMethodMayBeStatic")
class SpringSwaggerConfigSupport {

  def SpringSwaggerConfig springSwaggerConfig() {
    new SpringSwaggerConfig()
  }

  def Defaults defaults(ServletContext servletContext) {
    def typeResolver = new TypeResolver()
    def modelConfig = new SwaggerModelsConfiguration()
    def alternateTypeProvider = modelConfig.alternateTypeProvider(typeResolver)
    new Defaults(servletContext, typeResolver, alternateTypeProvider)
  }
}
