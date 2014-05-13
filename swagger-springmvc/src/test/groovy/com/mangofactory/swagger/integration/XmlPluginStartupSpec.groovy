package com.mangofactory.swagger.integration

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration("classpath:default-plugin-context.xml")
@WebAppConfiguration
class XmlPluginStartupSpec extends Specification {

  def "Should startup xml based configuration "() {
    expect:
      true
  }

}
