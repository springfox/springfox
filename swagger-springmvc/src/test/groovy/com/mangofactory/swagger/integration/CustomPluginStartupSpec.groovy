package com.mangofactory.swagger.integration

import com.mangofactory.swagger.configuration.CustomJavaPluginConfig
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = CustomJavaPluginConfig.class)
class CustomPluginStartupSpec extends Specification {

  @Autowired
  WebApplicationContext context;

  def "Should startup "() {
    when:
      MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
      MvcResult petApi = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get('/api-docs?group=customPlugin')).andReturn()
      MvcResult demoApi = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get('/api-docs?group=secondCustomPlugin')).andReturn()
    then:
      asJson(petApi).apis.size() == 4
      asJson(demoApi).apis.size() == 1
  }

  def asJson(MvcResult result){
    new JsonSlurper().parseText(result.response.getContentAsString())
  }
}
