package springdox.documentation.builders

import spock.lang.Specification

class TokenEndpointBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new TokenEndpointBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod | value         | property
      'url'         | 'urn:token'   | 'url'
      'tokenName'   | 'mytoken'     | 'tokenName'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new TokenEndpointBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod | value         | property
      'url'         | 'urn:token'   | 'url'
      'tokenName'   | 'mytoken'     | 'tokenName'
  }
}
