package com.mangofactory.swagger.core
import com.mangofactory.springmvc.plugins.DocumentationContextBuilder
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.mangofactory.swagger.readers.ApiOperationReader
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([DocumentationContextSupport, SpringSwaggerConfigSupport])
public class DocumentationContextSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  SwaggerSpringMvcPlugin plugin
  ApiOperationReader operationReader
  Defaults defaultValues

  def setup() {
    defaultValues = defaults(Mock(ServletContext))
    contextBuilder = defaultContextBuilder(defaultValues)
    plugin = new SwaggerSpringMvcPlugin()
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.build(contextBuilder)
  }
}
