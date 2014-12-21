package com.mangofactory.servicemodel

import com.mangofactory.servicemodel.builder.AuthorizationScopeBuilder

class AuthorizationScopeSpec extends InternalJsonSerializationSpec {

  final AuthorizationScope authorizationScope = new AuthorizationScopeBuilder().scope('s').description('d').build()

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
