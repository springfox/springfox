package springdox.documentation.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Unroll
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec
import springdox.documentation.swagger.mixins.SwaggerPluginsSupport

@Mixin([RequestMappingSupport, SwaggerPluginsSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  @Unroll
   def "should have correct response class"() {
    given:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), swaggerSchemaPlugins())
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationResponseClassReader operationResponseClassReader =
              new OperationResponseClassReader(new TypeResolver(), typeNameExtractor)

    when:
      operationResponseClassReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      if (operation.responseModel.collection) {
        assert expectedClass == String.format("%s[%s]", operation.responseModel.type, operation.responseModel.itemType)
      } else {
        assert expectedClass == operation.responseModel.type
      }

    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
      dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericPrimitiveArray')                | 'Array[byte]'
      dummyHandlerMethod('methodWithGenericComplexArray')                  | 'Array[DummyClass]'
      dummyHandlerMethod('methodWithEnumResponse')                         | 'string'
   }

}
