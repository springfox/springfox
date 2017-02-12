/*
 *
 *  Copyright 2017 the original author or authors.
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

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.context.WebApplicationContext
import spock.lang.Unroll
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.ApiListing
import springfox.documentation.service.Documentation
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.configuration.WebContextLoader
import springfox.documentation.spring.web.json.JsonSerializer
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

@ContextConfiguration(classes=[Swagger1ControllerConfiguration], loader = WebContextLoader)
@Mixin([JsonSupport, ApiListingSupport, AuthSupport])
@ActiveProfiles("Swagger1Controller")
class Swagger1ControllerSpec extends DocumentationContextSpec {

  MockMvc mockMvc

  @Autowired
  Swagger1Controller controller

  @Autowired
  WebApplicationContext context

  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner

  def setup() {
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingScanner = Mock(ApiListingScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(newHashMap())
    listingScanner.scan(_) >> LinkedListMultimap.create()

    mockMvc = webAppContextSetup(context).build()
  }

  @Unroll("path: #path")
  def "should return the default or first swagger resource listing"() {
    given:
      ApiDocumentationScanner sut = new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(sut.scan(context()))
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
      Multimap<String, ApiListing> listings = LinkedListMultimap.<String, ApiListing>create()
      listings.put('businesses', apiListing())
    and:
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .apiListingsByResourceGroupName(listings)
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
      authTypes.add(authorizationTypes())
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .resourceListing(resourceListing(authTypes))
              .build()

      controller.documentationCache.addDocumentation(group)
    when:
      MvcResult result = mockMvc.perform(get("/api-docs?group=groupName")).andDo(print()).andReturn()
      def json = jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200
      assertDefaultAuth(json)
  }

  @Configuration
  @EnableWebMvc
  @Profile("Swagger1Controller")
  private static class Swagger1ControllerConfiguration implements MapperSupport {

    @Bean
    static PropertySourcesPlaceholderConfigurer properties() throws Exception {
      final PropertySourcesPlaceholderConfigurer configurer =
          new PropertySourcesPlaceholderConfigurer()
      configurer.setPlaceholderPrefix("\$SPRINGFOX{")
      configurer.setIgnoreUnresolvablePlaceholders(false)
      return configurer
    }

    @Bean
    protected Swagger1Controller controller() {
      new Swagger1Controller(
          new DocumentationCache(),
          serviceMapper(),
          new JsonSerializer([new SwaggerJacksonModule()]))
    }
  }

}