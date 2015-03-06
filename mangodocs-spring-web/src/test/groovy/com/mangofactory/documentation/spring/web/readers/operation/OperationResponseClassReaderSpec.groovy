package com.mangofactory.documentation.spring.web.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import org.springframework.web.bind.annotation.RequestMethod

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
