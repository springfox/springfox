package com.mangofactory.swagger.models.dto

class AuthorizationScopeSpec extends InternalJsonSerializationSpec {

  final AuthorizationScope authorizationScope = new AuthorizationScope('s', 'd')

  def "should serialize"() {
    expect:
      writePretty(authorizationScope) == """{
  "description" : "d",
  "scope" : "s"
}"""
  }

  def "should pass coverage"() {
    expect:
      authorizationScope.description
      authorizationScope.scope
  }
}
