package springfox.documentation.swagger2.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.wordnik.swagger.models.Swagger
import spock.lang.Specification
import springfox.documentation.schema.configuration.ObjectMapperConfigured

class Swagger2DocumentationConfigurationSpec extends Specification {
  def "when event is fired the swagger jackson2 module is registered" () {
    given:
      Swagger2DocumentationConfiguration config = new Swagger2DocumentationConfiguration()
      ObjectMapper objectMapper = new ObjectMapper()
    and:
      ObjectMapperConfigured event = new ObjectMapperConfigured(this, objectMapper)
    when:
      config.onApplicationEvent(event)
      config.onApplicationEvent(event)
    then:
      objectMapper.findMixInClassFor(Swagger) != null
  }
}
