package springdox.documentation.spring.web

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.service.ResourceGroupingStrategy
import springdox.documentation.spring.web.mixins.RequestMappingSupport

@Mixin(RequestMappingSupport)
class SpringGroupingStrategySpec extends Specification {

  def "group paths and descriptions"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anything')
      ResourceGroupingStrategy strategy = new SpringGroupingStrategy()

      def groups = strategy.getResourceGroups(requestMappingInfo, handlerMethod)

    expect:
      groups.eachWithIndex { group, index ->
        group.groupName == groupNames[index]
      }
      strategy.getResourceDescription(requestMappingInfo, handlerMethod) == description
      strategy.getResourcePosition(requestMappingInfo, handlerMethod) == 0

    where:
      handlerMethod                                         | groupNames              | description
      dummyHandlerMethod()                                  | ["dummy-class"]         | "Dummy Class"
      dummyControllerHandlerMethod()                        | ["dummy-controller"]    | "Dummy Controller"
      petServiceHandlerMethod()                             | ["pets"]                | "Pet Service"
      fancyPetServiceHandlerMethod()                        | ["fancypets"]           | "Fancy Pet Service"
      multipleRequestMappingsHandlerMethod()                | ["pets", "petgrooming"] | "Pet Grooming Service"
      dummyHandlerMethod('methodWithRatherLongRequestPath') | ["dummy-class"]         | "Dummy Class"
  }

  def "Supports any documentation type" () {
    given:
      def sut = new SpringGroupingStrategy()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }
}
