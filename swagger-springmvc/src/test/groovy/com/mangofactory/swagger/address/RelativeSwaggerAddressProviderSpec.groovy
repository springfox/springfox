package com.mangofactory.swagger.address

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class RelativeSwaggerAddressProviderSpec extends Specification {

  @Unroll
  def "should produce relative addresses"() {
    given:
      ServletContext servletContext = Mock {
        0 * getContextPath() >> "/"
      }

    when:
      RelativeSwaggerAddressProvider provider = new RelativeSwaggerAddressProvider(servletContext)
      if (basePath) {
        provider.setBasePath(basePath)
      }

    then:
      provider.host == expectedHost
      provider.basePath == expectedBasePath
      provider.getOperationPath('/api/something/something') == expectedPath

    where:
      basePath      | expectedBasePath | expectedHost | expectedPath
      null          | null             | null         | '/api/something/something'
      '/v1'         | '/v1'            | null         | '/api/something/something'
      '/v1/private' | '/v1/private'    | null         | '/api/something/something'
  }
}
