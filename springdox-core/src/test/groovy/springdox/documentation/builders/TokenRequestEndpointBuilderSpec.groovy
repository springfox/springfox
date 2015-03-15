package springdox.documentation.builders

import spock.lang.Specification

class TokenRequestEndpointBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new TokenRequestEndpointBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value         | property
      'url'               | 'urn:token'   | 'url'
      'clientIdName'      | 'client1'     | 'clientIdName'
      'clientSecretName'  | 's3cr3t'      | 'clientSecretName'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new TokenRequestEndpointBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value         | property
      'url'               | 'urn:token'   | 'url'
      'clientIdName'      | 'client1'     | 'clientIdName'
      'clientSecretName'  | 's3cr3t'      | 'clientSecretName'
  }
}
