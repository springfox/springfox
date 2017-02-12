package springfox.documentation.swagger2.web

import com.google.common.collect.LinkedListMultimap
import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.*
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Unroll
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
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule
import springfox.documentation.swagger2.mappers.MapperSupport

import static com.google.common.collect.Maps.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*

@ContextConfiguration(classes=[Swagger1ControllerConfiguration], loader = WebContextLoader)
@Mixin([JsonSupport, ApiListingSupport, AuthSupport])
@ActiveProfiles("Swagger2Controller")
class Swagger2ControllerSpec extends DocumentationContextSpec implements MapperSupport {
  MockMvc mockMvc

  @Autowired
  Swagger2Controller controller
  @Autowired
  WebApplicationContext context

  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner

  def setup() {
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(newHashMap())
    listingScanner = Mock(ApiListingScanner)
    listingScanner.scan(_) >> LinkedListMultimap.create()

    mockMvc = webAppContextSetup(context).build();
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

  @Unroll("x-forwarded-prefix: #prefix")
  def "should respect proxy headers ('X-Forwarded-*') when setting host, port and basePath"() {
    given:
      ApiDocumentationScanner swaggerApiResourceListing =
          new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(context()))
    when:
      ResultActions result = mockMvc
          .perform(get("/v2/api-docs")
          .header("x-forwarded-host", "myhost:6060")
          .header("x-forwarded-prefix", prefix))

    then:
      result.andExpect(MockMvcResultMatchers.jsonPath("basePath").value(expectedPath))

    where:
      prefix        | expectedPath
      "/fooservice" | "/fooservice"
      "/"           | "/"
      ""            | "/"
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

  @Configuration
  @EnableWebMvc
  @Profile("Swagger2Controller")
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
    protected Swagger2Controller controller() {
      new Swagger2Controller(
          new DocumentationCache(),
          swagger2Mapper(),
          new JsonSerializer([new Swagger2JacksonModule()]))
    }
  }

}