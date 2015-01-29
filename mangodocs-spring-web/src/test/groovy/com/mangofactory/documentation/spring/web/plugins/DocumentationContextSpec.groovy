package com.mangofactory.documentation.spring.web.plugins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.Defaults
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder
import com.mangofactory.documentation.spring.web.mixins.DocumentationContextSupport
import com.mangofactory.documentation.spring.web.readers.operation.ApiOperationReader
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([DocumentationContextSupport])
public class DocumentationContextSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  DocumentationConfigurer plugin
  ApiOperationReader operationReader

  def setup() {
    contextBuilder = defaultContextBuilder()
    def defaultConfigurer = new DefaultConfiguration(new Defaults(), new TypeResolver(), Mock(ServletContext))
    defaultConfigurer.configure(contextBuilder)
    plugin = new DocumentationConfigurer(DocumentationType.SWAGGER_12)
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.configure(contextBuilder)
    contextBuilder.build()
  }
}
