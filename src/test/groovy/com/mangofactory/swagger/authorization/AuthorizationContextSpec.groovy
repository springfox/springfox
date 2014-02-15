package com.mangofactory.swagger.authorization

import com.mangofactory.swagger.mixins.AuthSupport
import spock.lang.Specification


@Mixin(AuthSupport)
class AuthorizationContextSpec extends Specification {

   def "scala authorizations"() {
    given:
      AuthorizationContext authorizationContext = new AuthorizationContext.AuthorizationContextBuilder(auth).build()
      authorizationContext.scalaAuthorizations
    expect:
      authorizationContext.getScalaAuthorizations().size() == expected

    where:
      auth          | expected
      defaultAuth() | 1
      []            | 0
   }
}
