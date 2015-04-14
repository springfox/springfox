package springfox.documentation.swagger2.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.wordnik.swagger.models.Swagger
import spock.lang.Specification

class Swagger2JacksonModuleSpec extends Specification {
  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    Swagger2JacksonModule.maybeRegisterModule(objectMapper)

    expect:
    objectMapper.findMixInClassFor(Swagger) != null
  }
}
