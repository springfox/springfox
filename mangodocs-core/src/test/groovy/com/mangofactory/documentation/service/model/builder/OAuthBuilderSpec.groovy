package com.mangofactory.documentation.service.model.builder

import com.mangofactory.documentation.service.model.AuthorizationScope
import com.mangofactory.documentation.service.model.GrantType
import spock.lang.Specification

class OAuthBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new OAuthBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                       | property
      'scopes'        | [Mock(AuthorizationScope)]  | 'scopes'
      'grantTypes'    | [Mock(GrantType)]           | 'grantTypes'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new OAuthBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                       | property
      'scopes'        | [Mock(AuthorizationScope)]  | 'scopes'
      'grantTypes'    | [Mock(GrantType)]           | 'grantTypes'
  }
}
