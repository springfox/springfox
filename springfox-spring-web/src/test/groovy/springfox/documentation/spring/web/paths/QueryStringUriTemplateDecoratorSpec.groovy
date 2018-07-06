package springfox.documentation.spring.web.paths

import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableValues
import springfox.documentation.service.Operation
import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import static java.util.Optional.*

class QueryStringUriTemplateDecoratorSpec extends DocumentationContextSpec {
  def "Is active only when the uri template is enabled" () {
    given:
      def sut = new QueryStringUriTemplateDecorator()
    expect:
      sut.supports(contextBuilder.enableUrlTemplating(true).build())
      !sut.supports(contextBuilder.enableUrlTemplating(false).build())
  }

  def "Creates path adjustment in relation to servlet mapping" () {
    given:
      def requestMappingContext = Mock(RequestMappingContext)
      PathContext ctx = new PathContext(requestMappingContext, operation(params, allowedValues))
    and:
      requestMappingContext.getDocumentationContext() >> documentationContext()
    and:
      def sut = new QueryStringUriTemplateDecorator()
    when:
      def decorator = sut.decorator(ctx)
    and:
      def decorated = decorator.apply(path)
    then:
      decorated == path + queryTemplate;
    where:
      path                 | params               | queryTemplate     | allowedValues
      ""                   | []                   | ""                | [test1:null]
      ""                   | ["test1"]            | "{?test1}"        | [test1:null]
      "/path"              | ["test2"]            | "{?test2}"        | [test1:null]
      "/path"              | ["test1", "test2"]   | "{?test1,test2}"  | [test1:null]
      "/path?test3=1"      | ["test1", "test2"]   | "{&test1,test2}"  | [test1:null]
      "/path?test3=1"      | ["test1", "test1"]   | "{&test1}"        | [test1:null]
      ""                   | []                   | ""                | [test1:[]]
      ""                   | ["test1"]            | "{?test1}"        | [test1:[]]
      "/path"              | ["test2"]            | "{?test2}"        | [test1:[]]
      "/path"              | ["test1", "test2"]   | "{?test1,test2}"  | [test1:[]]
      "/path?test3=1"      | ["test1", "test2"]   | "{&test1,test2}"  | [test1:[]]
      "/path?test3=1"      | ["test1", "test1"]   | "{&test1}"        | [test1:[]]
      ""                   | []                   | ""                | [test1:["1"]]
      ""                   | ["test1"]            | "?test1=1"        | [test1:["1"]]
      "/path"              | ["test2"]            | "{?test2}"        | [test1:["1"]]
      "/path"              | ["test1", "test2"]   | "?test1=1{&test2}"| [test1:["1"]]
      "/path?test3=1"      | ["test1", "test2"]   | "&test1=1{&test2}"| [test1:["1"]]
      "/path?test3=1"      | ["test1", "test1"]   | "&test1=1"        | [test1:["1"]]
      ""                   | []                   | ""                | [test1:["1", "2"]]
      ""                   | ["test1"]            | "{?test1}"        | [test1:["1", "2"]]
      "/path"              | ["test2"]            | "{?test2}"        | [test1:["1", "2"]]
      "/path"              | ["test1", "test2"]   | "{?test1,test2}"  | [test1:["1", "2"]]
      "/path?test3=1"      | ["test1", "test2"]   | "{&test1,test2}"  | [test1:["1", "2"]]
      "/path?test3=1"      | ["test1", "test1"]   | "{&test1}"        | [test1:["1", "2"]]
  }

  Optional<Operation> operation(List<String> paramNames, allowableValueLookup) {
    if (paramNames == null) {
      return empty()
    }
    def operation = Mock(Operation)
    operation.getParameters() >> paramNames.collect {
      new ParameterBuilder()
          .name(it)
          .parameterType("query")
          .allowableValues(allowableValue(allowableValueLookup[it]))
          .build() }
    of(operation)
  }

  AllowableValues allowableValue(values) {
    if (values) {
      new AllowableListValues(values, 'string')
    } else {
      null
    }
  }
}
