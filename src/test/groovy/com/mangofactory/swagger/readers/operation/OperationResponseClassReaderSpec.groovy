package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification
import spock.lang.Unroll

@Mixin(RequestMappingSupport)
class OperationResponseClassReaderSpec extends Specification {
   @Unroll
   def "should have correct response class"() {

    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader()

    when:
      operationResponseClassReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseClass'] == expectedClass
    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'

   }
}
