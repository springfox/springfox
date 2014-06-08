package com.mangofactory.swagger.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class JacksonScalaSupportSpec extends Specification {

  def "should add scala module based on boolean flag"() {
    given:
      ObjectMapper objectMapper = Mock()
    when:
      JacksonSwaggerSupport jacksonScalaSupport = new JacksonSwaggerSupport()
      jacksonScalaSupport.setSpringsMessageConverterObjectMapper(objectMapper)
    then:
      1 * objectMapper.registerModule(_)
  }
}
