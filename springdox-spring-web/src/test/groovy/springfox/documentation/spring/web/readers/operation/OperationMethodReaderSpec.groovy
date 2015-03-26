package springfox.documentation.spring.web.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.OperationContext

@Mixin([RequestMappingSupport])
class OperationMethodReaderSpec extends DocumentationContextSpec {


  def "should return api method when using default reader"() {

    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              currentHttpMethod, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      def operationMethodReader = new DefaultOperationBuilder();
    when:
      operationMethodReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()

    then:
      operation.method == expected
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | "GET"
      RequestMethod.PUT  | dummyHandlerMethod()                              | "PUT"
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | 'POST'
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | "POST"
  }

}
