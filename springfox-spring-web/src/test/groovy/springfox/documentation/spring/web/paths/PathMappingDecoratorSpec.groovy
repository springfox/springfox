package springfox.documentation.spring.web.paths
import com.google.common.base.Optional
import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class PathMappingDecoratorSpec extends DocumentationContextSpec {
  def "Creates path adjustment in relation to servlet mapping" () {
    given:
      def requestMappingContext = Mock(RequestMappingContext)
      PathContext ctx = new PathContext(requestMappingContext, Optional.absent())
    and:
      plugin.pathMapping(pathMapping)
      requestMappingContext.getDocumentationContext() >> context()
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
