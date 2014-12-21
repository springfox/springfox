package com.mangofactory.swagger.models.servicemodel

class BasicAuthSpec extends InternalJsonSerializationSpec {

  final BasicAuth basicAuth = new BasicAuth()

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
