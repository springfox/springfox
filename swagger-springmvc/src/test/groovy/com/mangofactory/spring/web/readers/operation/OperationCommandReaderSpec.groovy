package com.mangofactory.spring.web.readers.operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationNotesReader
import com.mangofactory.swagger.plugins.operation.OperationPositionReader
import com.mangofactory.swagger.plugins.operation.OperationSummaryReader
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Unroll

@Mixin([RequestMappingSupport])
class OperationCommandReaderSpec extends DocumentationContextSpec {
   private static final int CURRENT_COUNT = 3

   @Unroll("property #property expected: #expected")
   def "should set various properties based on method name or swagger annotation"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, CURRENT_COUNT, requestMappingInfo("somePath"),
              context(), "/anyPath")
    when:
      command.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation."$property" == expected
    where:
      command                         | property     | handlerMethod                              | expected
      new OperationSummaryReader()    | 'summary'    | dummyHandlerMethod('methodWithSummary')    | 'summary'
      new DefaultOperationBuilder()   | 'notes'      | dummyHandlerMethod()                       | 'dummyMethod'
      new OperationNotesReader()      | 'notes'      | dummyHandlerMethod('methodWithNotes')      | 'some notes'
      new DefaultOperationBuilder()   | 'nickname'   | dummyHandlerMethod()                       | 'dummyMethod'
      new DefaultOperationBuilder()   | 'position'   | dummyHandlerMethod()                       | CURRENT_COUNT
      new OperationPositionReader()   | 'position'   | dummyHandlerMethod('methodWithPosition')   | 5
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod('methodWithDeprecated') | 'true'
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod()                       | 'false'
   }
}
