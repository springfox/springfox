package com.mangofactory.swagger.controllers
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.dto.AuthorizationType
import com.mangofactory.swagger.dto.jackson.SwaggerJacksonProvider
import com.mangofactory.swagger.mixins.ApiListingSupport
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.JsonSupport
import com.mangofactory.swagger.mixins.MapperSupport
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*

@Mixin([JsonSupport, ApiListingSupport, AuthSupport, MapperSupport])
class DefaultSwaggerControllerSpec extends Specification {

  @Shared
  MockMvc mockMvc
  @Shared
  View mockView
  @Shared
  DefaultSwaggerController controller = new DefaultSwaggerController()
  @Shared
  SwaggerGlobalSettings settings = new SwaggerGlobalSettings()

  def setup() {
    def jackson2 = new MappingJackson2HttpMessageConverter()

    jackson2.setSupportedMediaTypes([MediaType.ALL, MediaType.APPLICATION_JSON])

    def mapper = new ObjectMapper()
    mapper.registerModule(new SwaggerJacksonProvider().swaggerJacksonModule())

    settings.setDtoMapper(serviceMapper())
    jackson2.setObjectMapper(mapper)
    mockMvc = standaloneSetup(controller)
            .setSingleView(mockView)
            .setMessageConverters(jackson2)
            .build();
  }

  @Unroll("path: #path")
  def "should return the default or first swagger resource listing"() {
    given:
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")

      swaggerApiResourceListing.swaggerGlobalSettings = settings;
      swaggerApiResourceListing.initialize()
      controller.swaggerCache = swaggerCache


    when:
      MvcResult result = mockMvc.perform(
              get(path)
      )
              .andDo(print()).andReturn()

      def responseJson = jsonBodyResponse(result)
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
      SwaggerCache swaggerCache = new SwaggerCache();
      swaggerCache.swaggerApiListingMap = [swaggerGroup: ['businesses': apiListing()]]
      controller.swaggerCache = swaggerCache
    when:
      MvcResult result = mockMvc.perform(get("/api-docs/swaggerGroup/businesses")).andDo(print()).andReturn()
      def responseJson = jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200
  }

  def "should respond with auth included"() {
    given:
      SwaggerCache swaggerCache = new SwaggerCache();

      def authTypes = new ArrayList<AuthorizationType>()
      authTypes.add(authorizationTypes());

      swaggerCache.swaggerApiResourceListingMap = [swaggerGroup: resourceListing(authTypes)]
      controller.swaggerCache = swaggerCache
    when:
      MvcResult result = mockMvc.perform(get("/api-docs?group=swaggerGroup")).andDo(print()).andReturn()
      def json = jsonBodyResponse(result)
      println json

    then:
      result.getResponse().getStatus() == 200
      assertDefaultAuth(json)
  }
}