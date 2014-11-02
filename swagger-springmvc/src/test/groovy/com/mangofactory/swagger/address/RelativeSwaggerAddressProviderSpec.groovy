package com.mangofactory.swagger.address

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class RelativeSwaggerAddressProviderSpec extends Specification {

   def "assert urls"(){
      given:
        ServletContext servletContext = Mock()
        RelativeSwaggerAddressProvider provider = new RelativeSwaggerAddressProvider(servletContext)
        servletContext.contextPath >> "/"

      expect:
        provider.applicationPath() == "/"
        provider.getDocumentationPath() == "/"
   }

}
