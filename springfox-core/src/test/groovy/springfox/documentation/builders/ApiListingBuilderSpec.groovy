package springfox.documentation.builders

import com.google.common.collect.Ordering
import spock.lang.Specification
import springfox.documentation.schema.Model
import springfox.documentation.service.ApiDescription
import springfox.documentation.service.Authorization

class ApiListingBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def orderingMock = Mock(Ordering)
      def sut = new ApiListingBuilder(orderingMock)
    and:
      orderingMock.sortedCopy(value) >> value
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                   | property
      'apiVersion'    | '1.0'                   | 'apiVersion'
      'basePath'      | 'urn:base-path'         | 'basePath'
      'resourcePath'  | 'urn:resource-path'     | 'resourcePath'
      'description'   | 'test'                  | 'description'
      'position'      | 1                       | 'position'
      'produces'      | ['app/json'] as Set     | 'produces'
      'consumes'      | ['app/json'] as Set     | 'consumes'
      'protocols'     | ['https']  as Set       | 'protocols'
      'authorizations'| [Mock(Authorization)]   | 'authorizations'
      'apis'          | [Mock(ApiDescription)]  | 'apis'
      'models'        | [m1: Mock(Model)]       | 'models'
  }

  def "Setting properties on the builder with null values preserves existing values"() {
    given:
      def orderingMock = Mock(Ordering)
      def sut = new ApiListingBuilder(orderingMock)
    and:
      orderingMock.sortedCopy(value) >> value
    when:
      sut."$builderMethod"(value)
    and:
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                   | property
      'apiVersion'    | '1.0'                   | 'apiVersion'
      'basePath'      | 'urn:base-path'         | 'basePath'
      'resourcePath'  | 'urn:resource-path'     | 'resourcePath'
      'description'   | 'test'                  | 'description'
      'produces'      | ['app/json'] as Set     | 'produces'
      'consumes'      | ['app/json'] as Set     | 'consumes'
      'protocols'     | ['https'] as Set        | 'protocols'
      'authorizations'| [Mock(Authorization)]   | 'authorizations'
      'apis'          | [Mock(ApiDescription)]  | 'apis'
      'models'        | [m1: Mock(Model)]       | 'models'
  }
}
