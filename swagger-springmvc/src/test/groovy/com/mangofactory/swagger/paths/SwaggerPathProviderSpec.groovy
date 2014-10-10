package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class SwaggerPathProviderSpec extends Specification {

  def "relative paths"() {
    given:
      ServletContext servletContext = Mock()
      servletContext.contextPath >> "/"
      SwaggerPathProvider provider = new RelativeSwaggerPathProvider(servletContext)
      provider.apiResourcePrefix = "some/prefix"

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
      ServletContext servletContext = Mock()
      servletContext.contextPath >> "/"
      SwaggerPathProvider provider = new RelativeSwaggerPathProvider(servletContext)
      provider.apiResourcePrefix = prefix
    then:
      thrown(IllegalArgumentException)
    where:
      prefix << [null, '/', '/api', '/api/', 'api/v1/', '/api/v1/']
  }

  @Unroll
  def "api declaration path"() {
    given:
      ServletContext servletContext = Mock()
      servletContext.contextPath >> contextPath
      SwaggerPathProvider provider = new RelativeSwaggerPathProvider(servletContext)
      provider.apiResourcePrefix = prefix
      provider.getOperationPath(apiDeclaration) == expected

    where:
      contextPath | prefix   | apiDeclaration           | expected
      '/'         | ""       | "/business/{businessId}" | "/business/{businessId}"
      '/'         | "api"    | "/business/{businessId}" | "/api/business/{businessId}"
      '/'         | "api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
      ''          | ""       | "/business/{businessId}" | "/business/{businessId}"
      ''          | "api"    | "/business/{businessId}" | "/api/business/{businessId}"
      ''          | "api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
  }

  def "should never return a path with duplicate slash"() {
    setup:
      RelativeSwaggerPathProvider swaggerPathProvider = new RelativeSwaggerPathProvider()

    when:
      String path = swaggerPathProvider.getResourceListingPath('/a', '/b')
      String opPath = swaggerPathProvider.getOperationPath('//a/b')
    then:
      path == '/a/b'
      opPath == path
  }

  def "should replace slashes"() {
    expect:
      new RelativeSwaggerPathProvider().sanitiseUrl(input) == expected
    where:
      input             | expected
      '//a/b'           | '/a/b'
      '//a//b//c'       | '/a/b/c'
      'http://some//a'  | 'http://some/a'
      'https://some//a' | 'https://some/a'
  }
}
