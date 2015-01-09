package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.web.AbsolutePathProvider
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class AbsoluteSwaggerPathProviderSpec extends Specification {

   def "assert urls"() {
      given:
        AbsolutePathProvider provider = new AbsolutePathProvider(servletContext: servletContext)

      expect:
        provider.applicationPath() == expectedAppPath
        provider.getDocumentationPath() == expectedDocPath

      where:
        servletContext   | expectedAppPath                      | expectedDocPath
        servletContext() | "http://localhost:8080/context-path" | "http://localhost:8080/context-path/api-docs"
        mockContext("")  | "http://localhost:8080"              | "http://localhost:8080/api-docs"

   }

   private mockContext(String path) {
      [getContextPath: { return path }] as ServletContext
   }
}
