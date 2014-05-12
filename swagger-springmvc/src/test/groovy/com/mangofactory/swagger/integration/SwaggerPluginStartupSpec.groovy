package com.mangofactory.swagger.integration

import com.mangofactory.swagger.configuration.TestSwaggerPluginConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = TestSwaggerPluginConfiguration.class)
class SwaggerPluginStartupSpec extends Specification{

  def "Should startup "(){
    expect:
      true
  }

}
