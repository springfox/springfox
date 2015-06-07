package springfox.documentation.swagger2.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.models.Swagger
import spock.lang.Specification

class Swagger2JacksonModuleSpec extends Specification {
  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    new Swagger2JacksonModule().maybeRegisterModule(objectMapper)

    expect:
    objectMapper.findMixInClassFor(Swagger) != null
  }

  def "should create serialization module only once"() {
    ObjectMapper objectMapper = new ObjectMapper()
    new Swagger2JacksonModule().maybeRegisterModule(objectMapper)
    new Swagger2JacksonModule().maybeRegisterModule(objectMapper)

    expect:
    objectMapper.findMixInClassFor(Swagger) != null
  }
}
