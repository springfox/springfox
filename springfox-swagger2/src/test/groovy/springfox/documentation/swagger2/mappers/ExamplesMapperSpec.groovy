package springfox.documentation.swagger2.mappers


import spock.lang.Specification
import springfox.documentation.schema.Example

class ExamplesMapperSpec extends Specification {
  def "examples are mapped correctly" () {
    given:
      def mediaType = "mediaType"
      def value = "value"
      def examples = new ArrayList<>()
      examples.add(new Example(mediaType, value))
    when:
      def sut = new ExamplesMapper()
    then:
      def mapped = sut.mapExamples(examples)
    and:
      mapped.size() == 1
      mapped.get(mediaType) == value
  }

  def "null mediaType is converted to empty string" () {
    given:
      def mediaType = null
      def value = "value"
      def examples = new ArrayList<>()
      examples.add(new Example(mediaType, value))
    when:
      def sut = new ExamplesMapper()
    then:
      def mapped = sut.mapExamples(examples)
    and:
      mapped.size() == 1
      mapped.get("") == value
  }

  def "empty example list maps to empty map" () {
    given:
      def examples = new ArrayList<>()
    when:
      def sut = new ExamplesMapper()
    then:
      def mapped = sut.mapExamples(examples)
    and:
      mapped.size() == 0
  }
}
