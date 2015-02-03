package com.mangofactory.documentation.builders

import spock.lang.Specification

class ResponseMessageBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ResponseMessageBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod     | value         | property
      'code'            | 200           | 'code'
      'message'         | 'OK'          | 'message'
      'responseModel'   | 'String'      | 'responseModel'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ResponseMessageBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod     | value         | property
      'message'         | 'OK'          | 'message'
      'responseModel'   | 'String'      | 'responseModel'
  }
}
