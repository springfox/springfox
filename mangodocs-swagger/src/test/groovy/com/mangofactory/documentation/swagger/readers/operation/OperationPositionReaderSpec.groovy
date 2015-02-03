package com.mangofactory.documentation.swagger.readers.operation

import com.mangofactory.documentation.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

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
