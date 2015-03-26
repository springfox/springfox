package springfox.documentation.swagger.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springfox.documentation.swagger.configuration.SwaggerJacksonModule
import springfox.documentation.swagger.dto.ApiListing

class SwaggerJacksonModuleSpec extends Specification {

  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    SwaggerJacksonModule.maybeRegisterModule(objectMapper)
    expect:
      objectMapper.findMixInClassFor(ApiListing) != null
  }
}
