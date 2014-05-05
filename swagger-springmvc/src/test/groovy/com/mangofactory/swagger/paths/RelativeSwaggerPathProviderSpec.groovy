package com.mangofactory.swagger.paths

import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class RelativeSwaggerPathProviderSpec extends Specification {

   def "assert urls"(){
      given:
        RelativeSwaggerPathProvider provider = new RelativeSwaggerPathProvider()

      expect:
        provider.applicationPath() == "/"
        provider.getDocumentationPath() == "/"
   }

}
