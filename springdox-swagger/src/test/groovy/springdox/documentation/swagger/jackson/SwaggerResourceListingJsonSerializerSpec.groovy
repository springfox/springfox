package springdox.documentation.swagger.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springdox.documentation.swagger.dto.ResourceListing

class SwaggerResourceListingJsonSerializerSpec extends Specification {

  def "should serialize a resource listing"() {
    ResourceListing resourceListing = Mock()
    JsonGenerator jsonGenerator = Mock()
    ObjectMapper objectMapper = Mock()
    def serializer = new SwaggerResourceListingJsonSerializer(objectMapper)

    when:
      serializer.serialize(resourceListing, jsonGenerator, null)
    then:
      1 * objectMapper.writeValueAsString(resourceListing)
  }
}
