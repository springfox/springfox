package springdox.documentation.swagger.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class OperationPositionReaderSpec extends DocumentationContextSpec {

  def "should have correct api position using swagger reader"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, contextCount, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationPositionReader operationPositionReader = new OperationPositionReader();
    when:
      operationPositionReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.position == expectedCount
    where:
      handlerMethod                            | contextCount  | expectedCount
      dummyHandlerMethod()                     | 2             | 0
      dummyHandlerMethod('methodWithPosition') | 3             | 5
  }
}
