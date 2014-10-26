package com.mangofactory.swagger.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.configuration.JacksonSwaggerSupport
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.mixins.ApiListingSupport
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.JsonSupport
import com.wordnik.swagger.models.Swagger
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@Mixin([JsonSupport, ApiListingSupport, AuthSupport])
class DefaultSwaggerControllerSpec extends Specification {

  @Shared
  MockMvc mockMvc
  @Shared
  View mockView
  @Shared
  DefaultSwaggerController controller = new DefaultSwaggerController()

  def setup() {
    def jackson2 = new MappingJackson2HttpMessageConverter()

    JacksonSwaggerSupport jacksonScalaSupport = new JacksonSwaggerSupport()
    ObjectMapper mapper = new ObjectMapper()
    mapper.registerModule(jacksonScalaSupport.swaggerSerializationModule())

    jackson2.setObjectMapper(mapper)
    mockMvc = standaloneSetup(controller)
            .setSingleView(mockView)
            .setMessageConverters(jackson2)
            .build();
  }

  @Unroll("path: #path expectedStatus: #expectedStatus")
  def "should return the default or first swagger resource listing"() {
    given:
      SwaggerCache swaggerCache = new SwaggerCache();
      swaggerCache.addSwaggerApi("default", Mock(Swagger))
      controller.swaggerCache = swaggerCache
    when:
      MvcResult result = mockMvc.perform(get(path)).andDo(print()).andReturn()
    then:
      result.getResponse().getStatus() == expectedStatus
    where:
      path                      | expectedStatus
      "/api-docs"               | 200
      "/api-docs?group=default" | 200
      "/api-docs?group=unknown" | 404
  }
}