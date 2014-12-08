package com.mangofactory.swagger.models.dto.jackson

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.VisibilityChecker
import spock.lang.Specification

class SwaggerJacksonProviderSpec extends Specification {

  def "should initialize object mapper with mapping features "() {
    SwaggerJacksonProvider swaggerJacksonProvider = new SwaggerJacksonProvider()
    VisibilityChecker checker = swaggerJacksonProvider.objectMapper.getVisibilityChecker()
    expect:
      checker._getterMinLevel == JsonAutoDetect.Visibility.NONE
      checker._setterMinLevel == JsonAutoDetect.Visibility.NONE
      checker._creatorMinLevel == JsonAutoDetect.Visibility.NONE
      checker._fieldMinLevel == JsonAutoDetect.Visibility.ANY
  }

  def "should create serialization module"() {
    SwaggerJacksonProvider swaggerJacksonProvider = new SwaggerJacksonProvider()
    Module module = swaggerJacksonProvider.swaggerJacksonModule()
    expect:
      module._serializers._classMappings.size() == 2

  }

  def "should configure serialization features"() {
    SwaggerJacksonProvider swaggerJacksonProvider = new SwaggerJacksonProvider()

    ObjectMapper objectMapper = Mock {
      1 * configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
      1 * configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      1 * setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
    expect:
      swaggerJacksonProvider.configureSerializationFeatures(objectMapper)
  }
}
