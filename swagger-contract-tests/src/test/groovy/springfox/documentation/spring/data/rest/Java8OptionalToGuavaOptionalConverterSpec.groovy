package springfox.documentation.spring.data.rest

import com.google.common.base.Optional
import spock.lang.Specification
import spock.lang.Unroll

class Java8OptionalToGuavaOptionalConverterSpec extends Specification {
  @Unroll
  def "Converts java 8 options to guava options"() {
    given:
    def sut = converter(isJdk8Optional)

    when:
    def converted = sut.convert(testObject)

    then:
    converted.isPresent() == expectedPresence
    converted.orNull() == expectedValue

    where:
    testObject       | isJdk8Optional | expectedPresence | expectedValue
    optional("test") | true           | true             | "test"
    optional(null)   | true           | false            | null
    "test"           | false          | true             | "test"
    null             | false          | false            | null

  }

  def optional(String value) {
    Optional.fromNullable(value)
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
