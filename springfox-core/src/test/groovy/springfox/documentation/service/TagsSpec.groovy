package springfox.documentation.service

import spock.lang.Specification

import static springfox.documentation.service.Tags.*

class TagsSpec extends Specification {
  def "Cannot instantiate Tags" () {
    when:
      new Tags()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Inspects a class with no ApiListings" () {
    given:
      def listings = new HashMap<>()
    expect:
      toTags(listings).isEmpty()
  }

  def "Empty tags predicate" () {
    given:
      def tags = ["", null, "test"]
    expect:
      tags.stream().filter(emptyTags()).count() == 1
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
