package com.mangofactory.documentation.service.model.builder

import com.mangofactory.documentation.service.model.AuthorizationScope
import spock.lang.Specification

class AuthorizationBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new AuthorizationBuilder()
      AuthorizationScope [] authScopes = new AuthorizationScope[1]
      authScopes[0] = Mock(AuthorizationScope)
    and:
      sut.type('oAuth')
      sut.scopes(authScopes)
    when:
      def built = sut.build()
    then:
      built.type == 'oAuth'
      built.scopes.size() == 1
  }

  def "Throws NPE when the scopes are not set"() {
    given:
      def sut = new AuthorizationBuilder()
    and:
      sut.type(null)
    when:
      sut.build()
    then:
      thrown(NullPointerException)
  }

  def "Preserves initialized type when setting null values"() {
    given:
      def sut = new AuthorizationBuilder()
      AuthorizationScope [] authScopes = new AuthorizationScope[1]
      authScopes[0] = Mock(AuthorizationScope)
      sut.scopes(authScopes)
    when:
      sut.type('oAuth')
      sut.type(null)
    and:
      def built = sut.build()
    then:
      built.type == 'oAuth'
      built.scopes.size() == 1
  }

}
