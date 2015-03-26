package springfox.documentation.spring.web

import spock.lang.Specification
import springfox.documentation.spring.web.mixins.RequestMappingSupport

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
