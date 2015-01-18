package com.mangofactory.spring.web.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.schema.TypeNameExtractor
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.PluginsSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationResponseClassReader
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Unroll

@Mixin([RequestMappingSupport, PluginsSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  @Unroll
   def "should have correct response class"() {
    given:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(),  pluginsManager())
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader(
              defaultValues.typeResolver, defaultValues.alternateTypeProvider, typeNameExtractor)

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
