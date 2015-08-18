package springfox.documentation.swagger2.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.LinkedListMultimap
import com.jayway.jsonpath.JsonPath
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule
import springfox.documentation.swagger2.mappers.MapperSupport

import static com.google.common.collect.Maps.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*

@Mixin([JsonSupport, ApiListingSupport, AuthSupport])
class Swagger2ControllerSpec extends DocumentationContextSpec implements MapperSupport {
  @Shared
  MockMvc mockMvc
  @Shared
  View mockView
  @Shared
  Swagger2Controller controller = new Swagger2Controller()
  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner

  def setup() {
    controller.documentationCache = new DocumentationCache()
    controller.jsonSerializer = new JsonSerializer([new Swagger2JacksonModule()])
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(newHashMap())
    listingScanner = Mock(ApiListingScanner)
    listingScanner.scan(_) >> LinkedListMultimap.create()
    controller.mapper = swagger2Mapper()
    def jackson2 = new MappingJackson2HttpMessageConverter()

    jackson2.setSupportedMediaTypes([MediaType.ALL, MediaType.APPLICATION_JSON])

    def mapper = new ObjectMapper()
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
          new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
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
      path                         | expectedStatus
      "/v2/api-docs"               | 200
      "/v2/api-docs?group=default" | 200
      "/v2/api-docs?group=unknown" | 404
  }

  def "Should omit port number if it is -1"() {
    given:
      ApiDocumentationScanner swaggerApiResourceListing =
        new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(context()))
    and:
      controller.hostNameOverride = "DEFAULT"
    when:
      MvcResult result = mockMvc
        .perform(get("/v2/api-docs"))
        .andDo(print())
        .andReturn()
    and:
      //Need to find out why jsonPath mvc result matcher doesn't work
      String host = JsonPath.read(result.response.contentAsString, "\$.host")
      jsonBodyResponse(result)
    then:
      host == "localhost"
      result.getResponse().getStatus() == 200
  }

}