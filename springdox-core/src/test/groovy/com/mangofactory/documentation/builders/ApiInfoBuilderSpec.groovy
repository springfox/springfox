package com.mangofactory.documentation.builders

import spock.lang.Specification

class ApiInfoBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ApiInfoBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value                   | property
      'version'           | '1.0'                   | 'version'
      'title'             | 'title'                 | 'title'
      'termsOfServiceUrl' | 'urn:tos'               | 'termsOfServiceUrl'
      'description'       | 'test'                  | 'description'
      'contact'           | 'Contact'               | 'contact'
      'license'           | 'license'               | 'license'
      'licenseUrl'        | 'urn:license'           | 'licenseUrl'
  }
}
