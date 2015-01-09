package com.mangofactory.swagger.core
import com.mangofactory.spring.web.plugins.DocumentationContextBuilder
import com.mangofactory.spring.web.plugins.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.spring.web.plugins.DocumentationConfigurer
import com.mangofactory.spring.web.readers.ApiOperationReader
import com.mangofactory.swagger.web.ClassOrApiAnnotationResourceGrouping
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([DocumentationContextSupport, SpringSwaggerConfigSupport])
public class DocumentationContextSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  DocumentationConfigurer plugin
  ApiOperationReader operationReader
  Defaults defaultValues

  def setup() {
    defaultValues = defaults(Mock(ServletContext))
    contextBuilder = defaultContextBuilder(defaultValues)
            .withResourceGroupingStrategy(new ClassOrApiAnnotationResourceGrouping())
    plugin = new DocumentationConfigurer()
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.build(contextBuilder)
  }
}
