package com.mangofactory.swagger.integration

import com.mangofactory.swagger.configuration.DefaultJavaPluginConfig
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = DefaultJavaPluginConfig.class)
class DefaultPluginStartupSpec extends Specification{

  def "Should startup "(){
    expect:
      true
  }

}
