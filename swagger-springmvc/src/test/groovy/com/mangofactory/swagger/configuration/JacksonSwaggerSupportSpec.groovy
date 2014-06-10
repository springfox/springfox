package com.mangofactory.swagger.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.DefaultModelPropertiesProvider
import org.springframework.context.ApplicationContext
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import spock.lang.Specification

class JacksonSwaggerSupportSpec extends Specification {

  def "Should register swagger module and set object mapper on DefaultModelPropertiesProvider"() {
    given:
      JacksonSwaggerSupport jacksonSwaggerSupport = new JacksonSwaggerSupport()
      ObjectMapper objectMapper = Mock()
      MappingJackson2HttpMessageConverter jacksonMessageConverter = Mock()
      jacksonMessageConverter.getObjectMapper() >> objectMapper

      ApplicationContext applicationContext = Mock()

      DefaultModelPropertiesProvider defaultModelPropertiesProvider = Mock()
      applicationContext.getBeansOfType(_) >> ['beanName': defaultModelPropertiesProvider]

      RequestMappingHandlerAdapter requestMappingHandlerAdapter = Mock()
      requestMappingHandlerAdapter.getMessageConverters() >> [jacksonMessageConverter]

      jacksonSwaggerSupport.requestMappingHandlerAdapter = requestMappingHandlerAdapter
      jacksonSwaggerSupport.applicationContext = applicationContext

    when:
      jacksonSwaggerSupport.setup()
    then:
      1 * objectMapper.registerModule(_)
      1 * defaultModelPropertiesProvider.setObjectMapper(objectMapper)
  }
}
