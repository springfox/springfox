package com.mangofactory.swagger.address

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class AbsoluteSwaggerAddressProviderSpec extends Specification {

   def "assert urls"() {
      given:
        AbsoluteSwaggerAddressProvider provider = new AbsoluteSwaggerAddressProvider(servletContext: servletContext)

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
