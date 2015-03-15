package com.mangofactory.documentation.spring.web.plugins
import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.Ordering
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.Defaults
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder
import com.mangofactory.documentation.spring.web.readers.operation.ApiOperationReader
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.documentation.spi.service.contexts.Orderings.nickNameComparator

public class DocumentationContextSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  DocumentationConfigurer plugin
  ApiOperationReader operationReader
  private defaultConfiguration

  def setup() {
    defaultConfiguration = new DefaultConfiguration(new Defaults(), new TypeResolver(), Mock(ServletContext))

    contextBuilder = this.defaultConfiguration.create(DocumentationType.SWAGGER_12)
            .handlerMappings([])
            .operationOrdering(Ordering.from(nickNameComparator()))
    plugin = new DocumentationConfigurer(DocumentationType.SWAGGER_12)
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.configure(contextBuilder)
  }
}
