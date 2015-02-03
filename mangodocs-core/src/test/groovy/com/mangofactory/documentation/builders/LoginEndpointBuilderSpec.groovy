package com.mangofactory.documentation.builders

import spock.lang.Specification

class LoginEndpointBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new LoginEndpointBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value       | property
      'url'           | 'url:login' | 'url'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new LoginEndpointBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value       | property
      'url'           | 'url:login' | 'url'
  }
}
