package com.mangofactory.swagger.models.dto.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.dto.ApiListing
import spock.lang.Specification

class SwaggerJacksonModuleSpec extends Specification {

  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    SwaggerJacksonModule.maybeRegisterModule(objectMapper)
    expect:
      objectMapper.findMixInClassFor(ApiListing) != null
  }
}
