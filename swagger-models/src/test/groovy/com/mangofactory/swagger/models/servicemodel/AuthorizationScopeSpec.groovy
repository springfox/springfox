package com.mangofactory.swagger.models.servicemodel

import com.mangofactory.swagger.models.servicemodel.builder.AuthorizationScopeBuilder

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
