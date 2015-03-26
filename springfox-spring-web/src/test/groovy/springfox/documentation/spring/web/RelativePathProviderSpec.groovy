package springfox.documentation.spring.web

import spock.lang.Specification
import springfox.documentation.spring.web.mixins.RequestMappingSupport

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class RelativePathProviderSpec extends Specification {

  def "relative paths"() {
    given:
      ServletContext servletContext = Mock(ServletContext)
      servletContext.contextPath >> "/"
      AbstractPathProvider provider = new RelativePathProvider(servletContext)
//      provider.apiResourcePrefix = "some/prefix"

    expect:
      provider.getApplicationBasePath() == "/"
      provider.getResourceListingPath('default', 'api-declaration') == "/default/api-declaration"
  }



//  def "Invalid prefix's"() {
//    when:
//      ServletContext servletContext = Mock(ServletContext)
//      servletContext.contextPath >> "/"
//      PathProvider provider = new RelativePathProvider(servletContext)
////      provider.apiResourcePrefix = prefix
//    then:
//      thrown(IllegalArgumentException)
//    where:
//      prefix << [null, '/', '/api', '/api/', 'api/v1/', '/api/v1/']
//  }

//  def "api declaration path"() {
//    given:
//      ServletContext servletContext = Mock(ServletContext)
//      servletContext.contextPath >> contextPath
//      PathProvider provider = new RelativePathProvider(servletContext)
////      provider.apiResourcePrefix = prefix
//      provider.getOperationPath(apiDeclaration) == expected
//
//    where:
//      contextPath | prefix   | apiDeclaration           | expected
//      '/'         | ""       | "/business/{businessId}" | "/business/{businessId}"
//      '/'         | "api"    | "/business/{businessId}" | "/api/business/{businessId}"
//      '/'         | "api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
//      ''          | ""       | "/business/{businessId}" | "/business/{businessId}"
//      ''          | "api"    | "/business/{businessId}" | "/api/business/{businessId}"
//      ''          | "api/v1" | "/business/{businessId}" | "/api/v1/business/{businessId}"
//  }

  def "should never return a path with duplicate slash"() {
    setup:
      RelativePathProvider swaggerPathProvider = new RelativePathProvider(servletContext())

    when:
      String path = swaggerPathProvider.getResourceListingPath('/a', '/b')
      String opPath = swaggerPathProvider.getOperationPath('//a/b')
    then:
      path == '/a/b'
      opPath == path
  }

  def "should replace slashes"() {
    expect:
      Paths.sanitiseUrl(input) == expected
    where:
      input             | expected
      '//a/b'           | '/a/b'
      '//a//b//c'       | '/a/b/c'
      'http://some//a'  | 'http://some/a'
      'https://some//a' | 'https://some/a'
  }
}
