package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class SwaggerPathProviderSpec extends Specification {

   def "relative paths"() {
      given:
        SwaggerPathProvider provider = new RelativeSwaggerPathProvider(apiResourcePrefix: "some/prefix")

      expect:
        provider.getApplicationBasePath() == "/"
        provider.getResourceListingPath('default', 'api-declaration') == "/default/api-declaration"
   }

   def "Absolute paths"() {
      given:
        SwaggerPathProvider provider = new AbsoluteSwaggerPathProvider(apiResourcePrefix: "", servletContext: servletContext())

      expect:
        provider.getApplicationBasePath() == expectedAppBase
        provider.getResourceListingPath(swaggerGroup, apiDeclaration) == expectedDoc

      where:
        swaggerGroup    | apiDeclaration     | expectedAppBase                      | expectedDoc
        'default'       | 'api-declaration'  | "http://localhost:8080/context-path" | "http://localhost:8080/context-path/api-docs/default/api-declaration"
        'somethingElse' | 'api-declaration2' | "http://localhost:8080/context-path" | "http://localhost:8080/context-path/api-docs/somethingElse/api-declaration2"

   }

   def "Invalid prefix's"() {
      when:
        new RelativeSwaggerPathProvider(apiResourcePrefix: prefix)
      then:
        thrown(IllegalArgumentException)
      where:
        prefix << [null, '/', '/api', '/api/', 'api/v1/', '/api/v1/']
   }

   def "api declaration path"() {
      expect:
        new RelativeSwaggerPathProvider(apiResourcePrefix: prefix).getOperationPath(apiDeclaration) == expected

      where:
        prefix   | apiDeclaration           | expected
        ""       | "/business/{businessId}" | "/business/{businessId}"
        "api"    | "/business/{businessId}" | "/api/business/{businessId}"
        "api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
   }
}
