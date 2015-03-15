package com.mangofactory.documentation.spring.web.readers.operation
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.SpringGroupingStrategy
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Shared

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
