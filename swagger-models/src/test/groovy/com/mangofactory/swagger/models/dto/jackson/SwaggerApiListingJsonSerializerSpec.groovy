package com.mangofactory.swagger.models.dto.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.dto.ApiListing
import spock.lang.Specification

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
