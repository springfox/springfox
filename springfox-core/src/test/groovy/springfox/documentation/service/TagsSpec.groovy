package springfox.documentation.service

import com.google.common.collect.LinkedListMultimap
import spock.lang.Specification

import static springfox.documentation.service.Tags.toTags

class TagsSpec extends Specification {
  def "Cannot instantiate Tags" () {
    when:
      new Tags()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Inspects a class with no ApiListings" () {
    given:
      def listings = LinkedListMultimap.create()
    expect:
      toTags(listings).isEmpty()
  }

  def "Inspects a class with an ApiListing with no Api annotations" () {
    given:
      def listings = LinkedListMultimap.create()
      def apiListing = Mock(ApiListing)
    when:
      listings.put("test", apiListing)
    and:
      apiListing.getTags() >> ["A", "B", ""]
      apiListing.getDescription() >> "description"
    then:
      toTags(listings) == [new Tag("A", "description"), new Tag("B", "description")] as Set
  }
}
