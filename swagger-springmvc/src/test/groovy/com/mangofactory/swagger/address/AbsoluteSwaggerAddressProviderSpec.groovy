package com.mangofactory.swagger.address

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class AbsoluteSwaggerAddressProviderSpec extends Specification {

  @Unroll
  def "should produce absolute addresses"() {
    given:
      AbsoluteSwaggerAddressProvider provider = new AbsoluteSwaggerAddressProvider(servletContext)
      if (null != basePath) {
        provider.setBasePath(basePath)
      }

    expect:
      provider.getHost() == expectedHost
      provider.getBasePath() == expectedBasePath

    where:
      basePath  | servletContext               | expectedHost     | expectedBasePath
      null      | mockContext("")              | "localhost:8080" | null
      null      | mockContext("/")             | "localhost:8080" | null
      null      | mockContext("a")             | "localhost:8080" | '/a'
      null      | mockContext("/a")            | "localhost:8080" | '/a'
      null      | mockContext("context-path")  | "localhost:8080" | "/context-path"
      null      | mockContext("/context-path") | "localhost:8080" | "/context-path"
      '/api/v1' | mockContext("")              | "localhost:8080" | '/api/v1'
      '/api/v1' | mockContext("/")             | "localhost:8080" | '/api/v1'
      '/api/v1' | mockContext("context-path")  | "localhost:8080" | "/context-path/api/v1"
      '/api/v1' | mockContext("/context-path") | "localhost:8080" | "/context-path/api/v1"

  }

  private mockContext(String path) {
    [getContextPath: { return path }] as ServletContext
  }
}
