package com.mangofactory.service.model

import com.mangofactory.documentation.service.ApiKey
import com.mangofactory.documentation.service.BasicAuth
import com.mangofactory.documentation.service.OAuth
import spock.lang.Specification

class AuthorizationTypeNamesSpec extends Specification {
  def "AuthorizationTypes have the correct names" () {
    expect:
      authType.getName() == expectedName
    where:
      authType                                | expectedName  |expectedType
      new ApiKey("api-key", "test", "header") | "api-key"     |"test"
      new OAuth("auth", [], [])               | "auth"        |"oauth2"
      new BasicAuth("basic")                  | "basic"       |"basicAuth"

  }
}
