/*
 *
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
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

  def "byte is represented as an integer with a format and minimum, maximum" () {
    when:
      def byteProp = property("byte")
    then:
      byteProp instanceof IntegerProperty
      byteProp.format == "int32"
      byteProp.maximum == Byte.MAX_VALUE
      byteProp.minimum == Byte.MIN_VALUE
  }

  def "List is represented as a string with a format" () {
    when:
      def listProp = property(new ModelRef("List", new ModelRef("string")))
    then:
      listProp instanceof ArrayProperty
  }

  def "Byte array is represented as a string with a byte format" () {
    when:
      def listProp = property(new ModelRef("Array", new ModelRef("byte")))
    then:
      listProp instanceof ByteArrayProperty
  }

  def "Map is represented as a string with a format" () {
    when:
      def mapProp = property(new ModelRef("Map", new ModelRef("string"), true))
    then:
      mapProp instanceof MapProperty
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
      "biginteger"| LongProperty
      "uuid"      | UUIDProperty
      "object"    | ObjectProperty
      "byte"      | IntegerProperty
      "INT"       | IntegerProperty
      "LONG"      | LongProperty
      "FLOAT"     | FloatProperty
      "DOUBLE"    | DoubleProperty
      "STRING"    | StringProperty
      "BOOLEAN"   | BooleanProperty
      "DATE"      | DateProperty
      "DATE-time" | DateTimeProperty
      "BIGDECIMAL"| DecimalProperty
      "BIGINTEGER"| LongProperty
      "UUID"      | UUIDProperty
      "OBJECT"    | ObjectProperty
      "BYTE"      | IntegerProperty
      "Anything"  | RefProperty
  }


}
