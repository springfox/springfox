package com.mangofactory.documentation.swagger.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.swagger.dto.ResourceListing
import spock.lang.Specification

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
