package springfox.documentation.swagger.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Ignore
import spock.lang.Specification
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ResourceListing
import springfox.documentation.spring.web.DocumentationCache

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ApiResourceControllerSpec extends Specification {
  def mockMvc
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
      uiConfiguration = new UiConfiguration("/validate")
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

  def "security Configuration is available" (){
    expect:
      mockMvc.perform(get("/configuration/security")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("{\"clientId\":\"client\",\"clientSecret\":\"client-secret\",\"realm\":\"real\"," +
          "\"appName\":\"test\",\"apiKey\":\"key\",\"apiKeyVehicle\":\"header\",\"scopeSeparator\":\",\",\"apiKeyName\":\"api_key\"}"))
  }

  def "UI Configuration is available" (){
    expect:
    mockMvc.perform(get("/configuration/ui")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("{\"validatorUrl\":\"/validate\",\"docExpansion\":\"none\",\"apisSorter\":\"alpha\",\"defaultModelRendering\":\"schema\",\"jsonEditor\":false,\"showRequestHeaders\":true}"))
  }

  def "Cache is available" (){
    expect:
      mockMvc.perform(get("/swagger-resources")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[{\"name\":\"test\",\"location\":\"/v1?group=test\",\"swaggerVersion\":\"1.2\"},{\"name\":\"test\",\"location\":\"/v2?group=test\",\"swaggerVersion\":\"2.0\"}]"))
  }

  @Ignore
  def "Cache is available when swagger controllers are not available" (){
    given:
      sut.swagger1Available = false
      sut.swagger2Available = false
    expect:
      mockMvc.perform(get("/swagger-resources")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[]"))
  }

  def "Verify that the property naming strategy does not affect output" () {
    given:
      ObjectMapper mapper = new ObjectMapper()
    when:
      mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    then:
      mapper.writer().writeValueAsString(sut.securityConfiguration) == "{\"clientId\":\"client\"," +
          "\"clientSecret\":\"client-secret\",\"realm\":\"real\",\"appName\":\"test\",\"apiKey\":\"key\",\"apiKeyVehicle\":\"header\",\"scopeSeparator\":\",\",\"apiKeyName\":\"api_key\"}"
      mapper.writer().writeValueAsString(sut.uiConfiguration) == "{\"validatorUrl\":\"/validate\",\"docExpansion\":\"none\",\"apisSorter\":\"alpha\",\"defaultModelRendering\":\"schema\",\"jsonEditor\":false,\"showRequestHeaders\":true}"
      mapper.writer().writeValueAsString(sut.swaggerResources().body) == "[{\"name\":\"test\"," +
          "\"location\":\"/v1?group=test\",\"swaggerVersion\":\"1.2\"},{\"name\":\"test\",\"location\":\"/v2?group=test\",\"swaggerVersion\":\"2.0\"}]"
  }
}
