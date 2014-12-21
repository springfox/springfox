package com.mangofactory.service.model.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.service.model.ResourceListing
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
