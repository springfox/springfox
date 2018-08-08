package springfox.documentation.spring.web.paths

import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import static java.util.Optional.*

class OperationPathDecoratorSpec extends DocumentationContextSpec {
  def "Creates path adjustment in relation to servlet mapping" () {
    given:
      def requestMappingContext = Mock(RequestMappingContext)
      PathContext ctx = new PathContext(requestMappingContext, empty())
    and:
      requestMappingContext.getDocumentationContext() >> documentationContext()
    and:
      def sut = new OperationPathDecorator()
    when:
      def decorator = sut.decorator(ctx)
    and:
      def decorated = decorator.apply(mappingPattern)
    then:
      decorated == expected
    where:
      mappingPattern             | expected
      ""                         | "/"
      "/"                        | "/"
      "/businesses"              | "/businesses"
      "businesses"               | "/businesses"
      "/businesses/{businessId}" | "/businesses/{businessId}"
      "/foo/bar:{baz}"           | "/foo/bar:{baz}"
      "/foo:{foo}/bar:{baz}"     | "/foo:{foo}/bar:{baz}"
      "/foo/bar:{baz}"           | "/foo/bar:{baz}"
      "/foo//bar:{baz}"          | "/foo/bar:{baz}"
  }
}
