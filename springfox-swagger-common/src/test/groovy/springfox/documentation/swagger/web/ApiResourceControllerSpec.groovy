/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
  def security = "{\n" +
      "    \"apiKey\": \"key\",\n" +
      "    \"apiKeyName\": \"api_key\",\n" +
      "    \"apiKeyVehicle\": \"header\",\n" +
      "    \"appName\": \"test\",\n" +
      "    \"clientId\": \"client\",\n" +
      "    \"clientSecret\": \"client-secret\",\n" +
      "    \"realm\": \"real\",\n" +
      "    \"scopeSeparator\": \",\"\n" +
      "}"
  def ui = "{\n" +
      "    \"apisSorter\": \"alpha\",\n" +
      "    \"defaultModelRendering\": \"schema\",\n" +
      "    \"docExpansion\": \"none\",\n" +
      "    \"jsonEditor\": false,\n" +
      "    \"showRequestHeaders\": true,\n" +
      "    \"supportedSubmitMethods\": [\n" +
      "        \"get\",\n" +
      "        \"post\",\n" +
      "        \"put\",\n" +
      "        \"delete\",\n" +
      "        \"patch\"\n" +
      "    ],\n" +
      "    \"validatorUrl\": \"/validate\"\n" +
      "}"
  def resources = "[\n" +
      "        {\n" +
      "            \"name\": \"test\",\n" +
      "            \"location\": \"/v1?group=test\",\n" +
      "            \"swaggerVersion\": \"1.2\"\n" +
      "        },\n" +
      "        {\n" +
      "            \"name\": \"test\",\n" +
      "            \"location\": \"/v2?group=test\",\n" +
      "            \"swaggerVersion\": \"2.0\"\n" +
      "        }\n" +
      "    ]"

  def sut

  def setup() {
    sut = new ApiResourceController(inMemorySwaggerResources())
    sut.with {
      securityConfiguration = new SecurityConfiguration(
          "client",
          "client-secret",
          "real",
          "test",
          "key",
          ApiKeyVehicle.HEADER,
          "api_key",
          ",")
      uiConfiguration = new UiConfiguration("/validate", UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
    }
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build()
  }

  def inMemorySwaggerResources() {
    def swaggerResources = new InMemorySwaggerResourcesProvider(documentationCache())
    swaggerResources.with {
      swagger1Url = "/v1"
      swagger1Available = true

      swagger2Url = "/v2"
      swagger2Available = true
    }
    swaggerResources
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
        mapper.writer().writeValueAsString(sut.securityConfiguration),
        security,
        JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(
        mapper.writer().writeValueAsString(sut.uiConfiguration),
        ui,
        JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(
        mapper.writer().writeValueAsString(sut.swaggerResources().body),
        resources,
        JSONCompareMode.NON_EXTENSIBLE)
  }
}
