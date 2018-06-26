package springfox.documentation.swagger2.mappers


import com.google.common.collect.Lists
import spock.lang.Specification
import springfox.documentation.schema.Example

class ExamplesMapperSpec extends Specification {
  def "examples are mapped correctly" () {
    given:
      def mediaType = "mediaType"
      def value = "value"
      def examples = Lists.newArrayList(new Example(mediaType, value))
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
    def examples = Lists.newArrayList(new Example(mediaType, value))
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
    def examples = Lists.newArrayList()
    when:
    def sut = new ExamplesMapper()
    then:
    def mapped = sut.mapExamples(examples)
    and:
    mapped.size() == 0
  }
}
