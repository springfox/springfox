package com.mangofactory.documentation.builder
import com.google.common.collect.Ordering
import com.mangofactory.documentation.schema.Model
import com.mangofactory.documentation.service.model.ApiDescription
import com.mangofactory.documentation.service.model.Authorization
import spock.lang.Specification

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
      'produces'      | ['app/json']            | 'produces'
      'consumes'      | ['app/json']            | 'consumes'
      'protocols'     | ['https']               | 'protocols'
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
      'produces'      | ['app/json']            | 'produces'
      'consumes'      | ['app/json']            | 'consumes'
      'protocols'     | ['https']               | 'protocols'
      'authorizations'| [Mock(Authorization)]   | 'authorizations'
      'apis'          | [Mock(ApiDescription)]  | 'apis'
      'models'        | [m1: Mock(Model)]       | 'models'
  }
}
