package com.mangofactory.swagger.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import spock.lang.Specification

class JacksonSwaggerSupportSpec extends Specification {

  @Ignore
  def "should add scala module based on boolean flag"() {
    given:
      JacksonSwaggerSupport jacksonSwaggerSupport = new JacksonSwaggerSupport()
      ObjectMapper objectMapper = Mock()
      jacksonSwaggerSupport.springsMessageConverterObjectMapper = objectMapper

    when:
      jacksonSwaggerSupport.setup()
    then:
      1 * objectMapper.registerModule(_)
  }
}
