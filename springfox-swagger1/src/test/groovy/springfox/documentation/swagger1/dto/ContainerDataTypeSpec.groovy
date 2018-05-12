/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.swagger1.dto

class ContainerDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize a primitive"() {
    given:
      def type = new ContainerDataType("int", false)
    expect:
      writePretty(type) == '''{
  "items" : {
    "format" : "int32",
    "type" : "integer"
  },
  "type" : "array"
}'''
    type.type == "array"
    type.items.absoluteType == new DataType("int").absoluteType
  }

  def "should serialize a complex"() {
    expect:
      writePretty(new ContainerDataType("pet", false)) == '''{
  "items" : {
    "type" : "pet"
  },
  "type" : "array"
}'''
  }

  def "should serialize a complex with uniqueItems"() {
    expect:
      writePretty(new ContainerDataType("pet", true)) == '''{
  "items" : {
    "type" : "pet"
  },
  "type" : "array",
  "uniqueItems" : true
}'''
  }

  def "should fail to serialize a nested array"() {
    when:
      new ContainerDataType("array", false)
    then:
      thrown(IllegalArgumentException)
  }

  def "should fail to serialize a null"() {
    when:
      new ContainerDataType(null, false)
    then:
      thrown(IllegalArgumentException)
  }
}
