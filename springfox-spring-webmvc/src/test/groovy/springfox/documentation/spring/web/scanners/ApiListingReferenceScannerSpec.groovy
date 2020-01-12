/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import com.fasterxml.classmate.TypeResolver
import springfox.documentation.RequestHandler
import springfox.documentation.annotations.ApiIgnore
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyController
import springfox.documentation.spring.web.mixins.AccessorAssertions
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import static springfox.documentation.builders.PathSelectors.*
import static springfox.documentation.builders.RequestHandlerSelectors.*

class ApiListingReferenceScannerSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        AccessorAssertions {

  ApiListingReferenceScanner sut = new ApiListingReferenceScanner()
  List<RequestHandler> requestHandlers
  def methodResolver = new HandlerMethodResolver(new TypeResolver())


  def setup() {
    requestHandlers = [Mock(RequestHandler)]
    contextBuilder.requestHandlers(requestHandlers)
            .withResourceGroupingStrategy(new SpringGroupingStrategy())
    plugin
            .pathProvider(new DefaultPathProvider())
            .select()
              .apis(withClassAnnotation(ApiIgnore).negate())
              .paths(regex(".*?"))
              .build()
  }

  def "should not get expected exceptions with invalid constructor params"() {
    given:
      contextBuilder.requestHandlers(requestHandlers)

    when:
      plugin
              .groupName(groupName)
              .configure(contextBuilder)

    then:
      documentationContext().groupName == "default"
    where:
      handlerMappings              | resourceGroupingStrategy     | groupName | message
      [requestMappingInfo("path")] | null                         | null      | "resourceGroupingStrategy is required"
      [requestMappingInfo("path")] | new SpringGroupingStrategy() | null      | "groupName is required"
  }


  def "grouping of listing references using Spring grouping strategy"() {
    given:

    requestHandlers = [
        new WebMvcRequestHandler(
            Paths.ROOT,
            methodResolver,
              requestMappingInfo("/public/{businessId}"),
              dummyControllerHandlerMethod()),
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/inventoryTypes"), dummyHandlerMethod()),
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/accounts"), dummyHandlerMethod()),
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/employees"), dummyHandlerMethod()),
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/inventory"), dummyHandlerMethod()),
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/inventory/products"), dummyHandlerMethod())
      ]

    when:
      contextBuilder.requestHandlers(requestHandlers)
      contextBuilder.withResourceGroupingStrategy(new SpringGroupingStrategy())
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result = sut.scan(documentationContext())

    then:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-controller", DummyController)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "grouping of listing references using Class or Api Grouping Strategy"() {
    given:

      requestHandlers = [
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}"), dummyControllerHandlerMethod()),
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/inventoryTypes"), dummyHandlerMethod()),
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/accounts"), dummyHandlerMethod()),
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/employees"), dummyHandlerMethod()),
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/inventory"), dummyHandlerMethod()),
          new WebMvcRequestHandler(Paths.ROOT, methodResolver, requestMappingInfo("/public/{businessId}/inventory/products"), dummyHandlerMethod())
      ]

    when:
      contextBuilder.requestHandlers(requestHandlers)
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result = sut.scan(documentationContext())

    then:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-controller", DummyController)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

}