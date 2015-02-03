package com.mangofactory.documentation.spring.web.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Unroll

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  @Unroll
   def "should have correct response class"() {
    given:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(),  defaultSchemaPlugins())
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader(
              new TypeResolver(), typeNameExtractor)

    when:
      operationResponseClassReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.responseClass == expectedClass

    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
   }

}
