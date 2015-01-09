package com.mangofactory.swagger.mixins
import com.mangofactory.spring.web.plugins.DocumentationContextBuilder
import com.mangofactory.spring.web.plugins.DocumentationConfigurer

@Mixin(SpringSwaggerConfigSupport)
class DocumentationContextSupport {

  def defaultContext(servletContext) {
    DocumentationContextBuilder contextBuilder = defaultContextBuilder(defaults(servletContext))
    new DocumentationConfigurer()
            .groupName("groupName")
            .includePatterns(".*")
            .build(contextBuilder)
  }

  DocumentationConfigurer defaultPlugin() {
    new DocumentationConfigurer()
            .groupName("groupName")
            .includePatterns(".*")
  }

  DocumentationContextBuilder defaultContextBuilder(defaults) {
    new DocumentationContextBuilder(defaults)
            .withHandlerMappings([])
  }

}
