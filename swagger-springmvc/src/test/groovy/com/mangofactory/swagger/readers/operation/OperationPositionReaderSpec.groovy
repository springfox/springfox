package com.mangofactory.swagger.readers.operation

import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport,  SpringSwaggerConfigSupport, DocumentationContextSupport])
class OperationPositionReaderSpec extends Specification {

  DocumentationContext context  = defaultContext(Mock(ServletContext))
   def "should have correct api position after several invocations"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"), handlerMethod)
      OperationPositionReader operationPositionReader = new OperationPositionReader();
      context.put("currentCount", 0)
    when:
      numCalls.times { operationPositionReader.execute(context) }
      Map<String, Object> result = context.getResult()

    then:
      result['currentCount'] == expectedCurrentCount
      result['position'] == expectedLastPosition
    where:
      numCalls | handlerMethod                            | expectedCurrentCount | expectedLastPosition
      1        | dummyHandlerMethod()                     | 1                    | 0
      2        | dummyHandlerMethod()                     | 2                    | 1
      5        | dummyHandlerMethod()                     | 5                    | 4
      2        | dummyHandlerMethod('methodWithPosition') | 6                    | 5
   }
}