package springdox.documentation.spring.web.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class OperationPositionReaderSpec extends DocumentationContextSpec {

   def "should have correct api position using default reader"() {
    given:
          OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, contextCount, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      def operationPositionReader = new DefaultOperationBuilder();
    when:
      operationPositionReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.position == expectedCount
    where:
      handlerMethod                            | contextCount  | expectedCount
      dummyHandlerMethod()                     | 2             | 2
      dummyHandlerMethod('methodWithPosition') | 3             | 3
   }

}