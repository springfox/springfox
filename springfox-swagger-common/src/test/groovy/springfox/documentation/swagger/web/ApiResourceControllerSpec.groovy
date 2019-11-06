/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.swagger.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.http.MediaType
import org.springframework.mock.env.MockEnvironment
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ResourceListing
import springfox.documentation.spring.web.DocumentationCache

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ApiResourceControllerSpec extends Specification {
  def mockMvc
  def security = """{
    "clientId": "client",
    "clientSecret": "client-secret",
    "realm": "real",
    "appName": "test",
    "scopeSeparator": ",",
    "additionalQueryStringParams": {"string":"value","boolean":true,"int":1},
    "useBasicAuthenticationWithAccessCodeGrant": false,
    "enableCsrfSupport": true
}"""
  def ui = """{
    "apisSorter":"alpha",
    "jsonEditor":false,
    "showRequestHeaders":false, 
    "deepLinking": true,
    "displayOperationId": false,
    "defaultModelsExpandDepth": 1,
    "defaultModelExpandDepth": 1,
    "defaultModelRendering": "example",
    "displayRequestDuration": false,
    "docExpansion": "none",
    "filter": false,
    "maxDisplayedTags": 1000,
    "operationsSorter": "alpha",
    "showExtensions": false,
    "showCommonExtensions": false,
    "tagsSorter": "alpha",
    "supportedSubmitMethods":["get","put","post","delete","options","head","patch","trace"],
    "validatorUrl": "/validate"
}"""
  def resources = """[
        {
            "name": "test",
            "url": "/v1?group=test",
            "location": "/v1?group=test",
            "swaggerVersion": "1.2"
        },
        {
            "name": "test",
            "url": "/v2?group=test",
            "location": "/v2?group=test",
            "swaggerVersion": "2.0"
        }
    ]"""

  def sut

  def setup() {
    sut = new ApiResourceController(inMemorySwaggerResources())
    sut.with {
      securityConfiguration = SecurityConfigurationBuilder.builder()
          .clientId("client")
          .clientSecret("client-secret")
          .realm("real")
          .appName("test")
          .scopeSeparator(",")
          .additionalQueryStringParams(['string': 'value', 'boolean': true, 'int': 1])
          .useBasicAuthenticationWithAccessCodeGrant(false)
          .enableCsrfSupport(true)
          .build()
      uiConfiguration = UiConfigurationBuilder.builder()
          .deepLinking(true)
          .displayOperationId(false)
          .defaultModelsExpandDepth(1)
          .defaultModelExpandDepth(1)
          .defaultModelRendering(ModelRendering.EXAMPLE)
          .displayRequestDuration(false)
          .docExpansion(DocExpansion.NONE)
          .filter(false)
          .maxDisplayedTags(1000)
          .operationsSorter(OperationsSorter.ALPHA)
          .showExtensions(false)
          .showCommonExtensions(false)
          .tagsSorter(TagsSorter.ALPHA)
          .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
          .validatorUrl("/validate")
          .build()
    }
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build()
  }

  def inMemorySwaggerResources() {
    def resources = new InMemorySwaggerResourcesProvider(mockEnvironment(), documentationCache())
    resources.swagger1Available = true
    resources.swagger2Available = true
    resources
  }

  def mockEnvironment() {
    def environment = new MockEnvironment()
    environment.withProperty("springfox.documentation.swagger.v1.path", "/v1")
    environment.withProperty("springfox.documentation.swagger.v2.path", "/v2")
    environment
  }

  def documentationCache() {
    def cache = new DocumentationCache()
    ResourceListing listing = new ResourceListing("1.0", [], [], ApiInfo.DEFAULT)
    cache.addDocumentation(new DocumentationBuilder()
        .name("test")
        .basePath("/base")
        .resourceListing(listing)
        .build())
    cache
  }

  def "security Configuration is available"() {
    expect:
    mockMvc.perform(get("/swagger-resources/configuration/security")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().json(security))
  }

  def "UI Configuration is available"() {
    expect:
    mockMvc.perform(get("/swagger-resources/configuration/ui")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().json(ui))
  }

  def "Cache is available"() {
    expect:
    mockMvc.perform(get("/swagger-resources")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().json(resources))
  }

  def "Verify that the property naming strategy does not affect output"() {
    given:
    ObjectMapper mapper = new ObjectMapper()
    when:
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    then:
    JSONAssert.assertEquals(
        security,
        mapper.writer().writeValueAsString(sut.securityConfiguration),
        JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(
        ui,
        mapper.writer().writeValueAsString(sut.uiConfiguration),
        JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(
        resources,
        mapper.writer().writeValueAsString(sut.swaggerResources().body),
        JSONCompareMode.NON_EXTENSIBLE)
  }
}
