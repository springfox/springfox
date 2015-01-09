package com.mangofactory.swagger.core

import com.mangofactory.spring.web.ResourceGroupingStrategy
import com.mangofactory.spring.web.SpringGroupingStrategy
import com.mangofactory.swagger.mixins.RequestMappingSupport
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

    where:
      handlerMethod                                         | groupNames              | description
      dummyHandlerMethod()                                  | ["dummy-class"]         | "Dummy Class"
      dummyControllerHandlerMethod()                        | ["dummy-controller"]    | "Dummy Controller"
      petServiceHandlerMethod()                             | ["pets"]                | "Pet Service"
      fancyPetServiceHandlerMethod()                        | ["fancypets"]           | "Fancy Pet Service"
      multipleRequestMappingsHandlerMethod()                | ["pets", "petgrooming"] | "Pet Grooming Service"
      dummyHandlerMethod('methodWithRatherLongRequestPath') | ["dummy-class"]         | "Dummy Class"

  }
}
