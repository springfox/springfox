package springfox.documentation.service

import com.google.common.collect.FluentIterable
import com.google.common.collect.LinkedListMultimap
import spock.lang.Specification

import static springfox.documentation.service.Tags.emptyTags
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

  def "Empty tags predicate" () {
    given:
      def tags = ["", null, "test"]
    expect:
      FluentIterable.from(tags).filter(emptyTags()).size() == 1
  }

}
