package com.mangofactory.documentation.spring.web

import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

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
