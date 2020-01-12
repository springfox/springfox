/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web

import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.wrapper.RequestMappingInfo

class SpringGroupingStrategySpec extends Specification implements RequestMappingSupport {

  def "group paths and descriptions"() {
    given:
      RequestMappingInfo requestMappingInfo = new WebMvcRequestMappingInfoWrapper(requestMappingInfo('/anything'))
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
