package com.mangofactory.swagger.authorization

import com.mangofactory.spring.web.plugins.AuthorizationContext
import com.mangofactory.swagger.mixins.AuthSupport
import spock.lang.Specification


@Mixin(AuthSupport)
class AuthorizationContextSpec extends Specification {

   def "scala authorizations"() {
    given:
      AuthorizationContext authorizationContext = AuthorizationContext.builder().withAuthorizations(auth).build()
      authorizationContext.scalaAuthorizations
    expect:
      authorizationContext.getScalaAuthorizations().size() == expected

    where:
      auth          | expected
      defaultAuth() | 1
      []            | 0
   }
}
