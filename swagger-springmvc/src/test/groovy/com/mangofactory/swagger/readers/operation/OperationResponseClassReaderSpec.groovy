package com.mangofactory.swagger.readers.operation
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Unroll

@Mixin([RequestMappingSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  @Unroll
   def "should have correct response class"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo("somePath"), handlerMethod)

      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader(
              defaultValues.typeResolver, defaultValues.alternateTypeProvider)

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
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
      dummyHandlerMethod('methodWithGenericPrimitiveArray')                | 'Array[byte]'
      dummyHandlerMethod('methodWithGenericComplexArray')                  | 'Array[DummyClass]'
      dummyHandlerMethod('methodWithEnumResponse')                         | 'string'

   }

}
