package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class SwaggerPathProviderSpec extends Specification {

   def "Should build paths"() {

      expect:
        provider.getDocumentationBasePath() == expectedDoc
        provider.getApplicationBasePath() == expectedBase

      where:
        provider                                                          | expectedBase                         | expectedDoc
        new RelativeSwaggerPathProvider(servletContext: servletContext()) | "/context-path"                      | "/context-path/api-docs"
        new AbsoluteSwaggerPathProvider(servletContext: servletContext()) | "http://127.0.0.1:8080/context-path" | "http://127.0.0.1:8080/context-path/api-docs"

   }
}
