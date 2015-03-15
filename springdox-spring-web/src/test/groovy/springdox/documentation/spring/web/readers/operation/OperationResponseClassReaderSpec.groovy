package springdox.documentation.spring.web.readers.operation

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  OperationResponseClassReader sut
  
  def setup() {
    def typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())

    sut = new OperationResponseClassReader(new TypeResolver(), typeNameExtractor)
  }
  
  def "Should support all documentation types"() {
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }
  def "should have correct response class"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
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
      dummyHandlerMethod('methodWithListOfBusinesses')                     | 'List[BusinessModel]'
  }

}
