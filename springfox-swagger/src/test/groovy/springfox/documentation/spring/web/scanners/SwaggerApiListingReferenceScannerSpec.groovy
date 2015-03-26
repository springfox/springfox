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

package springfox.documentation.spring.web.scanners

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import springfox.documentation.spring.web.mixins.AccessorAssertions
import springfox.documentation.swagger.web.ClassOrApiAnnotationResourceGrouping
import springfox.documentation.annotations.ApiIgnore
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spring.web.RelativePathProvider
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyController
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.swagger.web.AbsolutePathProvider

import javax.servlet.ServletContext

import static com.google.common.base.Predicates.*
import static springfox.documentation.builders.PathSelectors.*
import static springfox.documentation.builders.RequestHandlerSelectors.*

@Mixin([AccessorAssertions, RequestMappingSupport])
class SwaggerApiListingReferenceScannerSpec extends DocumentationContextSpec {

  ApiListingReferenceScanner sut = new ApiListingReferenceScanner()
  RequestMappingHandlerMapping requestMappingHandlerMapping

  def setup() {
    requestMappingHandlerMapping = Mock(RequestMappingHandlerMapping)
    contextBuilder.handlerMappings([requestMappingHandlerMapping])
      .withResourceGroupingStrategy(new ClassOrApiAnnotationResourceGrouping())
    plugin
            .pathProvider(new AbsolutePathProvider(servletContext()))
            .groupName("groupName")
            .select()
              .apis(not(withClassAnnotation(ApiIgnore)))
              .paths(regex(".*?"))
              .build()
  }

  def "should not get expected exceptions with invalid constructor params"() {
    given:
      contextBuilder.handlerMappings(handlerMappings)

    when:
      plugin
              .groupName(groupName)
              .configure(contextBuilder)

    then:
      context().groupName == "default"
    where:
      handlerMappings              | resourceGroupingStrategy                   | groupName | message
      [requestMappingInfo("path")] | null                                       | null      | "resourceGroupingStrategy is required"
      [requestMappingInfo("path")] | new ClassOrApiAnnotationResourceGrouping() | null      | "groupName is required"
  }

  def "should group controller paths"() {
    when:
      RequestMappingInfo businessRequestMappingInfo = requestMappingInfo("/api/v1/businesses")
      RequestMappingInfo accountsRequestMappingInfo = requestMappingInfo("/api/v1/accounts")

      requestMappingHandlerMapping.getHandlerMethods() >>
              [
                      (businessRequestMappingInfo): dummyHandlerMethod(),
                      (accountsRequestMappingInfo): dummyHandlerMethod()
              ]

      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.configure(contextBuilder)

      ApiListingReferenceScanResult result = sut.scan(context())

    then:
      result.getApiListingReferences().size() == 1
      ApiListingReference businessListingReference = result.getApiListingReferences()[0]
      businessListingReference.getPath() ==
              'http://localhost:8080/context-path/v1/api-docs/groupName/dummy-class'
  }

  def "grouping of listing references using Spring grouping strategy"() {
    given:

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}"))                   : dummyControllerHandlerMethod(),
              (requestMappingInfo("/public/inventoryTypes"))                 : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/accounts"))          : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/employees"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory/products")): dummyHandlerMethod()
      ]

    when:
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      contextBuilder.withResourceGroupingStrategy(new SpringGroupingStrategy())
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result= sut.scan(context())

    then:
      result.apiListingReferences.size() == 2
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Controller' })

    and:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-controller", DummyController)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "grouping of listing references using Class or Api Grouping Strategy"() {
    given:

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}"))                   : dummyControllerHandlerMethod(),
              (requestMappingInfo("/public/inventoryTypes"))                 : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/accounts"))          : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/employees"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory/products")): dummyHandlerMethod()
      ]

    when:
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result= sut.scan(context())

    then:
      result.apiListingReferences.size() == 2
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      result.apiListingReferences.find({ it.getDescription() == 'Group name' })

    and:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("group-name", DummyController, 2)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "Relative Paths"() {
    given:
      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}")): dummyControllerHandlerMethod()
      ]

    when:
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.pathProvider(new RelativePathProvider(Mock(ServletContext)))
      List<ApiListingReference> apiListingReferences = sut.scan(context()).apiListingReferences

    then: "api-docs should not appear in the path"
      ApiListingReference apiListingReference = apiListingReferences[0]
      apiListingReference.getPath() == "/groupName/group-name"
  }
}