package springfox.documentation.swagger.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.OperationContext

@Mixin([RequestMappingSupport])
class OperationHttpMethodReaderSpec extends DocumentationContextSpec {
  def "should return api method annotation when present"() {

    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              currentHttpMethod, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationHttpMethodReader operationMethodReader = new OperationHttpMethodReader();
    when:
      operationMethodReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()

    then:
      operation.method == expected
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | null
      RequestMethod.PUT  | dummyHandlerMethod()                              | null
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | 'GET'
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | null
  }
}
