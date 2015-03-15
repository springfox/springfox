package springdox.documentation.spring.web.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

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
