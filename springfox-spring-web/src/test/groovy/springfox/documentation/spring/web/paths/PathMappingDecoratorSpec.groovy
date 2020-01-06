package springfox.documentation.spring.web.paths

import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import static java.util.Optional.*

class PathMappingDecoratorSpec extends DocumentationContextSpec {
  def "Creates path adjustment in relation to servlet mapping" () {
    given:
      def requestMappingContext = Mock(RequestMappingContext)
      PathContext ctx = new PathContext(requestMappingContext, empty())
    and:
      plugin.pathMapping(pathMapping)
      requestMappingContext.getDocumentationContext() >> documentationContext()
    and:
      def sut = new PathMappingDecorator()
    when:
      def decorator = sut.decorator(ctx)
    and:
      def decorated = decorator.apply("test")
    then:
      decorated == prefix + "test";
    where:
      pathMapping   | prefix
      null          | "/"
      ""            | ""
      "pathMapping" | "pathMapping"
  }
}
