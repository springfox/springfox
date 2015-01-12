package com.mangofactory.service.model

import spock.lang.Specification

class AuthorizationTypeNamesSpec extends Specification {
  def "AuthorizationTypes have the correct names" () {
    expect:
      authType.getName() == expectedName
    where:
      authType                      | expectedName
      new ApiKey("test", "header")  | "test"
      new OAuth([], [])             | "oauth2"
      new BasicAuth()               | "basicAuth"

  }
}
