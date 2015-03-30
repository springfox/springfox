package com.mangofactory.swagger.models.dto

class TokenRequestEndpointSpec extends InternalJsonSerializationSpec {

  public static final String URL = "http://petstore.swagger.io/oauth/requestToken"
  final TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint(
          URL,
          "client_id",
          "client_secret"
  )

  def "should serialize"() {
    expect:
      writePretty(tokenRequestEndpoint) == """{
  "clientIdName" : "client_id",
  "clientSecretName" : "client_secret",
  "url" : "http://petstore.swagger.io/oauth/requestToken"
}"""
  }

  def "should pass coverage"() {
    expect:
      tokenRequestEndpoint.clientIdName == 'client_id'
      tokenRequestEndpoint.clientSecretName == 'client_secret'
      tokenRequestEndpoint.url == URL
  }
}
