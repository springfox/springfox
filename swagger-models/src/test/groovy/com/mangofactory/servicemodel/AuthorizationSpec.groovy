package com.mangofactory.servicemodel

import com.mangofactory.servicemodel.builder.AuthorizationBuilder

class AuthorizationSpec extends InternalJsonSerializationSpec {

  final Authorization authorization = new AuthorizationBuilder()
          .type('atype')
          .scopes([new AuthorizationScope('s', 'd')] as AuthorizationScope[])
          .build()

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
