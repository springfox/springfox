package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class RelativeSwaggerPathProviderSpec extends Specification {

   def "assert urls"(){
      given:
        ServletContext servletContext = Mock()
        RelativeSwaggerPathProvider provider = new RelativeSwaggerPathProvider(servletContext)
        servletContext.contextPath >> "/"

      expect:
        provider.applicationPath() == "/"
        provider.getDocumentationPath() == "/"
   }

}
