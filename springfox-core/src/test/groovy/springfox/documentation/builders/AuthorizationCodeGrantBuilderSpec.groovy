package springfox.documentation.builders

import spock.lang.Specification
import springfox.documentation.service.TokenRequestEndpoint
import springfox.documentation.service.TokenEndpoint

class AuthorizationCodeGrantBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new AuthorizationCodeGrantBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod           | value                       | property
      'tokenRequestEndpoint'  | Mock(TokenRequestEndpoint)  | 'tokenRequestEndpoint'
      'tokenEndpoint'         | Mock(TokenEndpoint)         | 'tokenEndpoint'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new AuthorizationCodeGrantBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod           | value                       | property
      'tokenRequestEndpoint'  | Mock(TokenRequestEndpoint)  | 'tokenRequestEndpoint'
      'tokenEndpoint'         | Mock(TokenEndpoint)         | 'tokenEndpoint'
  }
}
