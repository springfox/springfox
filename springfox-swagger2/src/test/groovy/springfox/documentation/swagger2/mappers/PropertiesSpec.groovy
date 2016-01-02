package springfox.documentation.swagger2.mappers

import io.swagger.models.properties.*
import springfox.documentation.schema.ModelRef

import static springfox.documentation.swagger2.mappers.Properties.*

import spock.lang.Specification

class PropertiesSpec extends Specification {
  def "Properties is a static class" () {
    when:
      new Properties()
    then:
      thrown(UnsupportedOperationException)
  }

  def "void is represented as a null" () {
    expect:
      property("void") == null
  }

  def "byte is represented as a string with a format" () {
    when:
      def byteProp = property("byte")
    then:
      byteProp instanceof StringProperty
      byteProp.format == "byte"
  }

  def "Nested collection properties are supported" () {
    when:
      def prop = itemTypeProperty(ref)
    then:
      prop == expected
    where:
      ref                                                                | expected
      new ModelRef("string")                                             | new StringProperty()
      new ModelRef("List", new ModelRef("string"))                       | new ArrayProperty(new StringProperty())
      new ModelRef("List", new ModelRef("List", new ModelRef("string"))) | new ArrayProperty(new ArrayProperty(new StringProperty()))
  }

  def "Properties are inferred given the type as a string"() {
    expect:
      property(typeName).class.isAssignableFrom(expected)
    where:
      typeName    | expected
      "int"       | IntegerProperty
      "long"      | LongProperty
      "float"     | FloatProperty
      "double"    | DoubleProperty
      "string"    | StringProperty
      "boolean"   | BooleanProperty
      "date"      | DateProperty
      "date-time" | DateTimeProperty
      "bigdecimal"| DecimalProperty
      "biginteger"| DecimalProperty
      "uuid"      | UUIDProperty
      "object"    | ObjectProperty
      "byte"      | StringProperty
      "INT"       | IntegerProperty
      "LONG"      | LongProperty
      "FLOAT"     | FloatProperty
      "DOUBLE"    | DoubleProperty
      "STRING"    | StringProperty
      "BOOLEAN"   | BooleanProperty
      "DATE"      | DateProperty
      "DATE-time" | DateTimeProperty
      "BIGDECIMAL"| DecimalProperty
      "BIGINTEGER"| DecimalProperty
      "UUID"      | UUIDProperty
      "OBJECT"    | ObjectProperty
      "BYTE"      | StringProperty
      "Anything"  | RefProperty
  }


}
