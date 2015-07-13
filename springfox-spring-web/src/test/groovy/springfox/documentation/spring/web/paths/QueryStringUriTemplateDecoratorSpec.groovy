package springfox.documentation.spring.web.paths

import com.google.common.base.Optional
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.Operation
import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class QueryStringUriTemplateDecoratorSpec extends DocumentationContextSpec {
  def "Creates path adjustment in relation to servlet mapping" () {
    given:
      def requestMappingContext = Mock(RequestMappingContext)
      PathContext ctx = new PathContext(requestMappingContext, operation(params))
    and:
      requestMappingContext.getDocumentationContext() >> context()
    and:
      def sut = new QueryStringUriTemplateDecorator()
    when:
      def decorator = sut.decorator(ctx)
    and:
      def decorated = decorator.apply(path)
    then:
      decorated == path + queryTemplate;
    where:
      path                 | params               | queryTemplate
      ""                   | []                   | ""
      ""                   | ["test1"]            | "{?test1}"
      "/path"              | ["test2"]            | "{?test2}"
      "/path"              | ["test1", "test2"]   | "{?test1,test2}"
      "/path?test3=1"      | ["test1", "test2"]   | "{&test1,test2}"
      "/path?test3=1"      | ["test1", "test1"]   | "{&test1}"
  }

  Optional<Operation> operation(List<String> paramNames) {
    if (paramNames == null) {
      return Optional.absent()
    }
    def operation = Mock(Operation)
    operation.getParameters() >> paramNames.collect { new ParameterBuilder().name(it).parameterType("query").build() }
    Optional.of(operation)
  }
}
