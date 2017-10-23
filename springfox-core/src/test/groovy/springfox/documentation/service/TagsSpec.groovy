package springfox.documentation.service

import com.google.common.collect.FluentIterable
import com.google.common.collect.LinkedListMultimap
import spock.lang.Specification

import static springfox.documentation.service.Tags.emptyTags
import static springfox.documentation.service.Tags.tagComparator
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

  def "Comparator uses tag order" (Tag tag1, Tag tag2, expected) {
    expect:
      assert tagComparator().compare(tag1, tag2) == expected
    where:
      tag1                | tag2                | expected
      new Tag("B", "", 1) | new Tag("A", "", 2) | -1
      new Tag("B", "", 1) | new Tag("A", "", 1) | 1
      new Tag("A", "", 1) | new Tag("A", "", 1) | 0
      new Tag("A", "")    | new Tag("A", "")    | 0
      new Tag("A", "")    | new Tag("B", "")    | -1
      new Tag("B", "")    | new Tag("A", "")    | 1
  }
}
