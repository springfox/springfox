package com.mangofactory.documentation.service.model.builder

import spock.lang.Specification

class AuthorizationScopeBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new AuthorizationScopeBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod | value                     | property
      'scope'       | 'scope1'                  | 'scope'
      'description' | 'description of scope1'   | 'description'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new AuthorizationScopeBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod | value                     | property
      'scope'       | 'scope1'                  | 'scope'
      'description' | 'description of scope1'   | 'description'
  }
}
