package springdox.documentation.spring.web.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Shared
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.spi.service.ResourceGroupingStrategy
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.SpringGroupingStrategy
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class OperationTagsReaderSpec extends DocumentationContextSpec {
  @Shared ResourceGroupingStrategy groupingStrategy = new SpringGroupingStrategy()
  
  def "should have correct tags"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    and:
      OperationTagsReader sut = new OperationTagsReader(groupingStrategy)

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.tags.containsAll([group])

    where:
      handlerMethod                                                        | group
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | "dummy-class"
      dummyControllerHandlerMethod()                                       | "dummy-controller"
  }
}
