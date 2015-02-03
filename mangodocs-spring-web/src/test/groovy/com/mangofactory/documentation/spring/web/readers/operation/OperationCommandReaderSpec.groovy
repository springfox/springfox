package com.mangofactory.documentation.spring.web.readers.operation
import com.mangofactory.documentation.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
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
      new DefaultOperationBuilder()   | 'notes'      | dummyHandlerMethod()                       | 'dummyMethod'
      new DefaultOperationBuilder()   | 'nickname'   | dummyHandlerMethod()                       | 'dummyMethod'
      new DefaultOperationBuilder()   | 'position'   | dummyHandlerMethod()                       | CURRENT_COUNT
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod('methodWithDeprecated') | 'true'
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod()                       | 'false'
   }
}
