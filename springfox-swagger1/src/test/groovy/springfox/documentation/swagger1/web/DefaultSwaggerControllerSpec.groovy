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

package springfox.documentation.swagger1.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.Documentation
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger1.configuration.SwaggerJacksonModule
import springfox.documentation.swagger1.mixins.MapperSupport

import static com.google.common.collect.Maps.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*

@Mixin([JsonSupport, ApiListingSupport, AuthSupport, MapperSupport])
class DefaultSwaggerControllerSpec extends DocumentationContextSpec {

  @Shared
  MockMvc mockMvc
  @Shared
  View mockView
  @Shared
  DefaultSwaggerController controller = new DefaultSwaggerController()
  ApiListingReferenceScanner listingReferenceScanner

  def setup() {
    controller.documentationCache = new DocumentationCache()
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    controller.mapper = serviceMapper()
    def jackson2 = new MappingJackson2HttpMessageConverter()

    jackson2.setSupportedMediaTypes([MediaType.ALL, MediaType.APPLICATION_JSON])

    def mapper = new ObjectMapper()
    SwaggerJacksonModule.maybeRegisterModule(mapper)

    jackson2.setObjectMapper(mapper)
    mockMvc = standaloneSetup(controller)
            .setSingleView(mockView)
            .setMessageConverters(jackson2)
            .build();
  }

  @Unroll("path: #path")
  def "should return the default or first swagger resource listing"() {
    given:
      ApiDocumentationScanner swaggerApiResourceListing =
              new ApiDocumentationScanner(listingReferenceScanner, Mock(ApiListingScanner))
      controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(context()))
    when:
      MvcResult result = mockMvc
              .perform(get(path))
              .andDo(print())
              .andReturn()

      jsonBodyResponse(result)
    then:
      result.getResponse().getStatus() == expectedStatus
    where:
      path                      | expectedStatus
      "/api-docs"               | 200
      "/api-docs?group=default" | 200
      "/api-docs?group=unknown" | 404
  }

  def "should respond with api listing for a given resource group"() {
    given:
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .apiListingsByResourceGroupName(['businesses': apiListing()])
              .build()
      controller.documentationCache.addDocumentation(group)
    when:
      MvcResult result = mockMvc.perform(get("/api-docs/groupName/businesses")).andDo(print()).andReturn()
      jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200
  }

  def "should respond with auth included"() {
    given:
      def authTypes = new ArrayList<SecurityScheme>()
      authTypes.add(authorizationTypes());
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .resourceListing(resourceListing(authTypes))
              .build()

      controller.documentationCache.addDocumentation(group)
    when:
      MvcResult result = mockMvc.perform(get("/api-docs?group=groupName")).andDo(print()).andReturn()
      def json = jsonBodyResponse(result)
//      println json

    then:
      result.getResponse().getStatus() == 200
      assertDefaultAuth(json)
  }
}