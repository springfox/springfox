package com.mangofactory.swagger.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

@ContextConfiguration("classpath:handler-adapter-context.xml")
@WebAppConfiguration
class MultipleRequestMappingHandlerAdaptersSpec extends Specification{

  @Autowired
  WebApplicationContext context;

  def "should survive multiple MultipleRequestMappingHandlerAdapters"(){
    expect:
      true
  }
}
