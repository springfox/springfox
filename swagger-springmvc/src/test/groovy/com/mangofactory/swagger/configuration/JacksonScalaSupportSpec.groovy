package com.mangofactory.swagger.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import spock.lang.Specification

class JacksonScalaSupportSpec extends Specification {

  def "should add scala module based on boolean flag"() {
    given:
      RequestMappingHandlerAdapter requestMappingHandlerAdapter = Mock()
      ObjectMapper objectMapper = Mock()
      MappingJackson2HttpMessageConverter httpMessageConverter = Mock()
      httpMessageConverter.getObjectMapper() >> objectMapper
      requestMappingHandlerAdapter.getMessageConverters() >> [httpMessageConverter]
    when:
      JacksonScalaSupport jacksonScalaSupport = new JacksonScalaSupport(
              requestMappingHandlerAdapter: requestMappingHandlerAdapter
      )
      jacksonScalaSupport.init()
    then:
      1 * objectMapper.registerModule(_)
  }
}
