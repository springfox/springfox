package com.mangofactory.swagger.mixins
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin

@Mixin(SpringSwaggerConfigSupport)
class DocumentationContextSupport {

  def defaultContext(servletContext) {
    DocumentationContextBuilder contextBuilder = defaultContextBuilder(defaults(servletContext))
    new SwaggerSpringMvcPlugin()
            .swaggerGroup("swaggerGroup")
            .includePatterns(".*")
            .build(contextBuilder)
  }

  SwaggerSpringMvcPlugin defaultPlugin() {
    new SwaggerSpringMvcPlugin()
            .swaggerGroup("swaggerGroup")
            .includePatterns(".*")
  }

  DocumentationContextBuilder defaultContextBuilder(defaults) {
    new DocumentationContextBuilder(defaults)
            .withHandlerMappings([])
  }
}
