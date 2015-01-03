package com.mangofactory.swagger.controllers
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.service.model.AuthorizationType
import com.mangofactory.service.model.Group
import com.mangofactory.service.model.builder.GroupBuilder
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.dto.jackson.SwaggerJacksonProvider
import com.mangofactory.swagger.mixins.ApiListingSupport
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.JsonSupport
import com.mangofactory.swagger.mixins.MapperSupport
import com.mangofactory.swagger.scanners.ApiListingReferenceScanResult
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.mangofactory.swagger.scanners.ApiListingScanner
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Unroll

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
    contextBuilder = new DocumentationContextBuilder(defaultValues).withHandlerMappings([])
    controller.swaggerCache = new SwaggerCache()
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    controller.mapper = serviceMapper()
    def jackson2 = new MappingJackson2HttpMessageConverter()

    jackson2.setSupportedMediaTypes([MediaType.ALL, MediaType.APPLICATION_JSON])

    def mapper = new ObjectMapper()
    mapper.registerModule(new SwaggerJacksonProvider().swaggerJacksonModule())

    jackson2.setObjectMapper(mapper)
    mockMvc = standaloneSetup(controller)
            .setSingleView(mockView)
            .setMessageConverters(jackson2)
            .build();
  }

  @Unroll("path: #path")
  def "should return the default or first swagger resource listing"() {
    given:
      SwaggerApiResourceListing swaggerApiResourceListing =
              new SwaggerApiResourceListing(listingReferenceScanner, Mock(ApiListingScanner))
      controller.swaggerCache.addGroup(swaggerApiResourceListing.scan(context()))
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
      Group group = new GroupBuilder()
              .withName("swaggerGroup")
              .withApiListings(['businesses': apiListing()])
              .build()
      controller.swaggerCache.addGroup(group)
    when:
      MvcResult result = mockMvc.perform(get("/api-docs/swaggerGroup/businesses")).andDo(print()).andReturn()
      jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200
  }

  def "should respond with auth included"() {
    given:
      def authTypes = new ArrayList<AuthorizationType>()
      authTypes.add(authorizationTypes());
      Group group = new GroupBuilder()
              .withName("swaggerGroup")
              .withResourceListing(resourceListing(authTypes))
              .build()

      controller.swaggerCache.addGroup(group)
    when:
      MvcResult result = mockMvc.perform(get("/api-docs?group=swaggerGroup")).andDo(print()).andReturn()
      def json = jsonBodyResponse(result)
      println json

    then:
      result.getResponse().getStatus() == 200
      assertDefaultAuth(json)
  }
}