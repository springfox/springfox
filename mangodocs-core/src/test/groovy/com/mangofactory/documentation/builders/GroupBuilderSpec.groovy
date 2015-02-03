package com.mangofactory.documentation.builders

import com.mangofactory.documentation.service.ApiListing
import com.mangofactory.documentation.service.ResourceListing
import spock.lang.Specification

class GroupBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new GroupBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod        | value                          | property
      'name'               | 'group1'                       | 'groupName'
      'apiListingsByGroup' | [group1: [Mock(ApiListing)]]   | 'apiListings'
      'resourceListing'    | Mock(ResourceListing)          | 'resourceListing'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new GroupBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod         | value                           | property
      'name'                | 'group1'                        | 'groupName'
      'apiListingsByGroup'  | [group1: [Mock(ApiListing)]]    | 'apiListings'
      'resourceListing'     | Mock(ResourceListing)           | 'resourceListing'
  }
}
