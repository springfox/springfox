package com.mangofactory.documentation.swagger.dto

class TokenEndpointSpec extends InternalJsonSerializationSpec {

  final TokenEndpoint tokenEndpoint = new TokenEndpoint("u", "tok")

  def "should serialize"() {
    expect:
      writePretty(tokenEndpoint) == """{
  "tokenName" : "tok",
  "url" : "u"
}"""
  }

  def "should pass coverage"() {
    expect:
      tokenEndpoint.tokenName
      tokenEndpoint.url
  }
}
