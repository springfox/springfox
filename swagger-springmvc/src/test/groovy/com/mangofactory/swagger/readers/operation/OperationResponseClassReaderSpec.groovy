package com.mangofactory.swagger.readers.operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Unroll

@Mixin([RequestMappingSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  @Unroll
   def "should have correct response class"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader(
              defaultValues.typeResolver, defaultValues.alternateTypeProvider)

    when:
      operationResponseClassReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.responseClass == expectedClass

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
