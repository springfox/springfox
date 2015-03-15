package springdox.documentation.builders

import spock.lang.Specification
import springdox.documentation.service.ApiInfo
import springdox.documentation.service.ApiListingReference
import springdox.documentation.service.AuthorizationType

class ResourceListingBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ResourceListingBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value                                 | property
      'apiVersion'        | "1.0"                                 | 'apiVersion'
      'apis'              | [Mock(ApiListingReference)]           | 'apis'
      'authorizations'    | [Mock(AuthorizationType)]             | 'authorizations'
      'info'              | ApiInfo.DEFAULT                       | 'info'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ResourceListingBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'apiVersion'        | "1.0"                                 | 'apiVersion'
      'apis'              | [Mock(ApiListingReference)]           | 'apis'
      'authorizations'    | [Mock(AuthorizationType)]             | 'authorizations'
      'info'              | ApiInfo.DEFAULT                       | 'info'
  }
}
