package springfox.documentation.swagger.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
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
  def sut = new ApiResourceController()
  def mockMvc = MockMvcBuilders.standaloneSetup(sut).build()

  def setup() {
    def cache = new DocumentationCache()
    sut.with {
      swagger1Url = "/v1"
      swagger2Url = "/v2"
      documentationCache = cache
      securityConfiguration = new SecurityConfiguration("client", "client-secret","real", "test", "key", ",")
      uiConfiguration = new UiConfiguration("/validate")
      swagger1Available = true
      swagger2Available = true
    }
    ResourceListing listing = new ResourceListing("1.0", [], [], ApiInfo.DEFAULT)
    cache.addDocumentation(new DocumentationBuilder()
        .name("test")
        .basePath("/base")
        .resourceListing(listing)
        .build())
  }

  def "security Configuration is available" (){
    expect:
      mockMvc.perform(get("/configuration/security")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("{\"clientId\":\"client\",\"clientSecret\":\"client-secret\",\"realm\":\"real\",\"appName\":\"test\",\"apiKey\":\"key\"}"))
  }

  def "UI Configuration is available" (){
    expect:
    mockMvc.perform(get("/configuration/ui")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("{\"validatorUrl\":\"/validate\"}"))
  }

  def "Cache is available" (){
    expect:
      mockMvc.perform(get("/swagger-resources")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[{\"name\":\"test\",\"location\":\"/v1?group=test\",\"swaggerVersion\":\"1.2\"},{\"name\":\"test\",\"location\":\"/v2?group=test\",\"swaggerVersion\":\"2.0\"}]"))
  }

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
      mapper.writer().writeValueAsString(sut.securityConfiguration) == "{\"clientId\":\"client\",\"clientSecret\":\"client-secret\",\"realm\":\"real\",\"appName\":\"test\",\"apiKey\":\"key\"}"
      mapper.writer().writeValueAsString(sut.uiConfiguration) == "{\"validatorUrl\":\"/validate\"}"
      mapper.writer().writeValueAsString(sut.swaggerResources().body) == "[{\"name\":\"test\"," +
          "\"location\":\"/v1?group=test\",\"swaggerVersion\":\"1.2\"},{\"name\":\"test\",\"location\":\"/v2?group=test\",\"swaggerVersion\":\"2.0\"}]"
  }
}
