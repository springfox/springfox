package com.mangofactory.swagger.models.dto

class ApiKeySpec extends InternalJsonSerializationSpec {

  final ApiKey headerKey = new ApiKey('myKey')
  final ApiKey customKey = new ApiKey('myKey', 'cookie')

  def "should produce a header key"() {
    expect:
      writePretty(headerKey) ==
              """{
  "keyname" : "myKey",
  "passAs" : "header",
  "type" : "apiKey"
}"""
  }

  def "should produce a custom key"() {
    expect:
      writePretty(customKey) ==
              """{
  "keyname" : "myKey",
  "passAs" : "cookie",
  "type" : "apiKey"
}"""
  }

  def "should pass coverage"() {
    expect:
      customKey.getKeyname() == 'myKey'
      customKey.getPassAs() == 'cookie'
      customKey.getType() == 'apiKey'
  }
}
