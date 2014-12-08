package com.mangofactory.swagger.models.dto

class AuthorizationSpec extends InternalJsonSerializationSpec {

  final Authorization authorization = new Authorization(
          'atype',
          [new AuthorizationScope('s', 'd')] as AuthorizationScope[]
  )

  def "should serialize"() {
    expect:
      writePretty(authorization) == """{
  "scopes" : [ {
    "description" : "d",
    "scope" : "s"
  } ],
  "type" : "atype"
}"""
  }

  def "should pass coverage"() {
    expect:
      authorization.getScopes().size() == 1
      authorization.getType() == 'atype'
  }
}
