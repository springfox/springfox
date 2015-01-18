package com.mangofactory.documentation.spring.web

import com.mangofactory.documentation.service.RequestMappingPatternMatcher
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder
import com.mangofactory.documentation.spring.web.mixins.DocumentationContextSupport
import com.mangofactory.documentation.spring.web.plugins.DocumentationConfigurer
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
            .withResourceGroupingStrategy(Mock(ResourceGroupingStrategy))
            .pathProvider(new RelativePathProvider(Mock(ServletContext)))
            .requestMappingPatternMatcher(Mock(RequestMappingPatternMatcher))
    plugin = new DocumentationConfigurer(DocumentationType.SWAGGER_12)
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.configure(contextBuilder)
    contextBuilder.build()
  }
}
