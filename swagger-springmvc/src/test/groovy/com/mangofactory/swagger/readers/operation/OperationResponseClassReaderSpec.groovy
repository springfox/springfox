package com.mangofactory.swagger.readers.operation

import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport,  SpringSwaggerConfigSupport, DocumentationContextSupport])
class OperationResponseClassReaderSpec extends Specification {
  DocumentationContext context  = defaultContext(Mock(ServletContext))
  Defaults defaultValues = defaults(Mock(ServletContext))
  @Unroll
   def "should have correct response class"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"), handlerMethod)

      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader(
              defaultValues.typeResolver, defaultValues.alternateTypeProvider)

    when:
      operationResponseClassReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseClass'] == expectedClass
    where:
      handlerMethod                                                        | expectedClass
//      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
//      dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'
//      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
//      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
      dummyHandlerMethod('methodWithGenericPrimitiveArray')                | 'Array[byte]'
      dummyHandlerMethod('methodWithGenericComplexArray')                  | 'Array[DummyClass]'
      dummyHandlerMethod('methodWithEnumResponse')                         | 'string'

   }

}
