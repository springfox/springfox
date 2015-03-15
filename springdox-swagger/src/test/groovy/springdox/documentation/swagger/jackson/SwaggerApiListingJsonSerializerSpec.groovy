package springdox.documentation.swagger.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springdox.documentation.swagger.dto.ApiListing

class SwaggerApiListingJsonSerializerSpec extends Specification {

  def "should serialize a resource listing"() {
    ApiListing apiListing = Mock()
    JsonGenerator jsonGenerator = Mock()
    ObjectMapper objectMapper = Mock()
    def serializer = new SwaggerApiListingJsonSerializer(objectMapper)

    when:
      serializer.serialize(apiListing, jsonGenerator, null)
    then:
      1 * objectMapper.writeValueAsString(apiListing)
      1 * jsonGenerator.writeRaw(_)
  }
}
