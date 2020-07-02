package springfox.documentation.swagger2.configuration

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.swagger.models.properties.Property
import spock.lang.Specification
import spock.lang.Unroll


class PropertyExampleSerializerMixinSpec extends Specification {
  def "detects string literals correctly"() {
    given:
    def property = Mock(Property)
    def generator = Mock(JsonGenerator)
    def sut = new Swagger2JacksonModule.PropertyExampleSerializerMixin.PropertyExampleSerializer(clazz)

    when:
    property.example >> example

    and:
    sut.serialize(example, generator, Mock(SerializerProvider))

    then:
    noCallsToWriteString * generator.writeString(_) >> { args ->
      assert args[0] == serializedExample
    }

    where:
    example   | clazz  | serializedExample | noCallsToWriteString
    "true"    | String | ""                | 0
    "false"   | String | ""                | 0
    "123"     | String | ""                | 0
    "-123"    | String | ""                | 0
    "123.3"   | String | ""                | 0
    "'123'"   | String | "123"             | 1
    "\"123\"" | String | "123"             | 1
    "test"    | String | "test"            | 1
    ""        | String | ""                | 1
    null      | String | null              | 0
  }
}
