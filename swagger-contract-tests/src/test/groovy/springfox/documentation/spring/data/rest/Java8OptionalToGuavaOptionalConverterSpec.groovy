package springfox.documentation.spring.data.rest

import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Optional.ofNullable

class Java8OptionalToGuavaOptionalConverterSpec extends Specification {
  @Unroll
  def "Converts java 8 options to guava options"() {
    given:
    def sut = converter(isJdk8Optional)

    when:
    def converted = sut.convert(testObject)

    then:
    converted.isPresent() == expectedPresence
    converted.orElse(null) == expectedValue

    where:
    testObject       | isJdk8Optional | expectedPresence | expectedValue
    optional("test") | true           | true             | "test"
    optional(null)   | true           | false            | null
    "test"           | false          | true             | "test"
    null             | false          | false            | null

  }

  def optional(String value) {
    ofNullable(value)
  }

  def converter(isJdk8Optional) {
    new Java8OptionalToGuavaOptionalConverter() {
      @Override
      boolean isJdk8Optional(Object source) {
        return isJdk8Optional
      }
    }
  }
}
