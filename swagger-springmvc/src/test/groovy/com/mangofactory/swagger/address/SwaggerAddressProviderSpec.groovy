package com.mangofactory.swagger.address

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class SwaggerAddressProviderSpec extends Specification {

  @Unroll
  def "Should throw with invalid prefix [#prefix]"() {
    when:
      ServletContext servletContext = Mock()
      servletContext.contextPath >> "/"
      SwaggerAddressProvider provider = new RelativeSwaggerAddressProvider(servletContext)
      provider.setBasePath(prefix)
    then:
      thrown(IllegalArgumentException)
    where:
      prefix << [null, '/', '/api/', 'api/v1/', '/api/v1/']
  }

  @Unroll
  def "Should generate operation paths"() {
    given:
      ServletContext servletContext = Mock()
      SwaggerAddressProvider provider = new RelativeSwaggerAddressProvider(servletContext)
      if (null != prefix) {
        provider.basePath = prefix

      }
      provider.getOperationPath(apiDeclaration) == expected

    where:
      prefix    | apiDeclaration           | expected
      null      | "/business/{businessId}" | "/business/{businessId}"
      null      | "/business/{businessId}" | "/business/{businessId}"
      "/api"    | "/business/{businessId}" | "/api/business/{businessId}"
      "/api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
      "/api"    | "/business/{businessId}" | "/api/business/{businessId}"
      "/api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
      "/api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
  }
}
