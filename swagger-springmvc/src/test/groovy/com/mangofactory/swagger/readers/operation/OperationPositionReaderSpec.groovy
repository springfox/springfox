package com.mangofactory.swagger.readers.operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationPositionReader
import org.springframework.web.bind.annotation.RequestMethod

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