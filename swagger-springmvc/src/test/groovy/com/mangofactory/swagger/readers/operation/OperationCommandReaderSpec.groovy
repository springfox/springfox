package com.mangofactory.swagger.readers.operation
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification
import spock.lang.Unroll

@Mixin(RequestMappingSupport)
class OperationCommandReaderSpec extends Specification {

   private static final int CURRENT_COUNT = 3

   @Unroll("property #property expected: #expected")
   def "should set various properties based on method name or swagger annotation"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      context.put("currentCount", CURRENT_COUNT)
    when:
      Command operationCommand = command
      operationCommand.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result[property] == expected
    where:
      command                         | property     | handlerMethod                              | expected
      new OperationSummaryReader()    | 'summary'    | dummyHandlerMethod()                       | 'dummyMethod'
      new OperationSummaryReader()    | 'summary'    | dummyHandlerMethod('methodWithSummary')    | 'summary'
      new OperationNotesReader()      | 'notes'      | dummyHandlerMethod()                       | 'dummyMethod'
      new OperationNotesReader()      | 'notes'      | dummyHandlerMethod('methodWithNotes')      | 'some notes'
      new OperationNicknameReader()   | 'nickname'   | dummyHandlerMethod()                       | 'dummyMethod'
      new OperationPositionReader()   | 'position'   | dummyHandlerMethod()                       | CURRENT_COUNT
      new OperationPositionReader()   | 'position'   | dummyHandlerMethod('methodWithPosition')   | 5
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod('methodWithDeprecated') | 'true'
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod()                       | 'false'
   }
}
