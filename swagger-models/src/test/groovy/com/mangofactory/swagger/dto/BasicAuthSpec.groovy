package com.mangofactory.swagger.dto

class BasicAuthSpec extends InternalJsonSerializationSpec {

  final AuthorizationType basicAuth = new BasicAuth()

  def "should serialize"() {
    expect:
      writePretty(basicAuth) == """{
  "type" : "basicAuth"
}"""
  }

  def "should pass coverage"() {
    expect:
      basicAuth.getName() == 'basicAuth'
      basicAuth.getType() == 'basicAuth'
  }
}
