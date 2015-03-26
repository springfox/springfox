package springfox.documentation.builders

import spock.lang.Specification
import springfox.documentation.service.ApiListing
import springfox.documentation.service.ResourceListing

class DocumentationBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new DocumentationBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      if (value instanceof Set) {
        assert built."$property".containsAll(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod                     | value                          | property
      'name'                            | 'group1'                       | 'groupName'
      'apiListingsByResourceGroupName'  | [group1: [Mock(ApiListing)]]   | 'apiListings'
      'resourceListing'                 | Mock(ResourceListing)          | 'resourceListing'
      'basePath'                        | 'urn:some-path'                | 'basePath'
      'produces'                        | ['application/json'] as Set    | 'produces'
      'consumes'                        | ['application/json'] as Set    | 'consumes'
      'schemes'                         | ['http']  as Set               | 'schemes'
      'tags'                            | ['pet'] as Set                 | 'tags'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new DocumentationBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      if (value instanceof Set) {
        assert built."$property".containsAll(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod                     | value                           | property
      'name'                            | 'group1'                        | 'groupName'
      'apiListingsByResourceGroupName'  | [group1: [Mock(ApiListing)]]    | 'apiListings'
      'resourceListing'                 | Mock(ResourceListing)           | 'resourceListing'
      'basePath'                        | 'urn:some-path'                 | 'basePath'
      'produces'                        | ['application/json'] as Set     | 'produces'
      'consumes'                        | ['application/json'] as Set     | 'consumes'
      'schemes'                         | ['http']  as Set                | 'schemes'
      'tags'                            | ['pet'] as Set                  | 'tags'
  }
}
