package com.mangofactory.documentation.swagger.web
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.service.AuthorizationType
import com.mangofactory.documentation.service.Documentation
import com.mangofactory.documentation.builders.DocumentationBuilder
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.DocumentationCache
import com.mangofactory.documentation.spring.web.mixins.ApiListingSupport
import com.mangofactory.documentation.spring.web.mixins.AuthSupport
import com.mangofactory.documentation.spring.web.mixins.JsonSupport
import com.mangofactory.documentation.spring.web.scanners.ApiDocumentationScanner
import com.mangofactory.documentation.spring.web.scanners.ApiListingReferenceScanResult
import com.mangofactory.documentation.spring.web.scanners.ApiListingReferenceScanner
import com.mangofactory.documentation.spring.web.scanners.ApiListingScanner
import com.mangofactory.documentation.swagger.jackson.SwaggerJacksonProvider
import com.mangofactory.documentation.swagger.mixins.MapperSupport
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
    controller.documentationCache = new DocumentationCache()
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
      "/v1/api-docs"               | 200
      "/v1/api-docs?group=default" | 200
      "/v1/api-docs?group=unknown" | 404
  }

  def "should respond with api listing for a given resource group"() {
    given:
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .apiListingsByResourceGroupName(['businesses': apiListing()])
              .build()
      controller.documentationCache.addDocumentation(group)
    when:
      MvcResult result = mockMvc.perform(get("/v1/api-docs/groupName/businesses")).andDo(print()).andReturn()
      jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200
  }

  def "should respond with auth included"() {
    given:
      def authTypes = new ArrayList<AuthorizationType>()
      authTypes.add(authorizationTypes());
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .resourceListing(resourceListing(authTypes))
              .build()

      controller.documentationCache.addDocumentation(group)
    when:
      MvcResult result = mockMvc.perform(get("/v1/api-docs?group=groupName")).andDo(print()).andReturn()
      def json = jsonBodyResponse(result)
      println json

    then:
      result.getResponse().getStatus() == 200
      assertDefaultAuth(json)
  }
}