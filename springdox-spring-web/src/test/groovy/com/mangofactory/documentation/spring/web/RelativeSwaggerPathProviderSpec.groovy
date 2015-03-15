package com.mangofactory.documentation.spring.web

import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.RelativePathProvider
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class RelativeSwaggerPathProviderSpec extends Specification {

   def "assert urls"(){
      given:
        ServletContext servletContext = Mock()
        RelativePathProvider provider = new RelativePathProvider(servletContext)
        servletContext.contextPath >> "/"

      expect:
        provider.applicationPath() == "/"
        provider.getDocumentationPath() == "/"
   }

}
