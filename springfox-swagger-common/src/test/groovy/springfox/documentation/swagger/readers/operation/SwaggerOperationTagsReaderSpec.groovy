package springfox.documentation.swagger.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.readers.operation.DefaultTagsProvider

@Mixin([RequestMappingSupport, ServicePluginsSupport, ServicePluginsSupport])
class SwaggerOperationTagsReaderSpec extends DocumentationContextSpec {
  def "should have correct tags"() {
    given:
    OperationContext operationContext =
        new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
        RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
        context(), "/anyPath")

    and:
      SwaggerOperationTagsReader sut = new SwaggerOperationTagsReader(new DefaultTagsProvider())

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()
    then:
    operation.tags.containsAll(tags)

    where:
    handlerMethod                                        | tags
    dummyHandlerMethod('methodWithConcreteResponseBody') | ["dummy-class"]
    dummyControllerHandlerMethod()                       | ["dummy-controller"]
    dummyOperationWithTags()                             | ["Tag1", "Tag2", "Tag3", "Tag4"]
  }
}
