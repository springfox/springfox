package com.mangofactory.documentation.spring.web.authorization

import com.mangofactory.documentation.RequestMappingPatternMatcher
import com.mangofactory.documentation.spi.service.contexts.AuthorizationContext
import com.mangofactory.documentation.spring.web.mixins.AuthSupport
import spock.lang.Specification


@Mixin(AuthSupport)
class AuthorizationContextSpec extends Specification {

   def "scala authorizations"() {
    given:
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(auth)
              .withRequestMappingPatternMatcher(Mock(RequestMappingPatternMatcher))
              .build()
      authorizationContext.scalaAuthorizations
    expect:
      authorizationContext.getScalaAuthorizations().size() == expected

    where:
      auth          | expected
      defaultAuth() | 1
      []            | 0
   }

}
