package com.mangofactory.documentation.builders

import com.mangofactory.documentation.service.ApiInfo
import com.mangofactory.documentation.service.ApiListingReference
import com.mangofactory.documentation.service.AuthorizationType
import spock.lang.Specification

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
