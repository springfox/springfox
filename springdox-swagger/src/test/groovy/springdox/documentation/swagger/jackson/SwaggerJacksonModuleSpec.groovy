package springdox.documentation.swagger.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springdox.documentation.swagger.configuration.SwaggerJacksonModule
import springdox.documentation.swagger.dto.ApiListing

class SwaggerJacksonModuleSpec extends Specification {

  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    SwaggerJacksonModule.maybeRegisterModule(objectMapper)
    expect:
      objectMapper.findMixInClassFor(ApiListing) != null
  }
}
