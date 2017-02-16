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

import spock.lang.Unroll

class DataTypeSpec extends InternalJsonSerializationSpec {
  @Unroll
  def "should serialize dataType[#dataType]"() {
    def swaggerDataType = new DataType(dataType)
    when:
      def json = writeAndParse(swaggerDataType)
    then:
      assertion.call(json)

    where:
      dataType     | assertion
      "void"       | { it.type == 'void' }
      "File"       | { it.type == 'File' }
      "__file"       | { it.type == 'File' }
      "int"        | { it.type == 'integer' && it.format == 'int32' }
      "long"       | { it.type == 'integer' && it.format == 'int64' }
      "float"      | { it.type == 'number' && it.format == 'float' }
      "double"     | { it.type == 'number' && it.format == 'double' }
      "Date"       | { it.type == 'string' && it.format == 'date-time' }
      "DateTime"   | { it.type == 'string' && it.format == 'date-time' }
      "string"     | { it.type == 'string' && !it.format }
      "bigdecimal" | { it.type == 'number' && !it.format }
      "biginteger" | { it.type == 'integer' && !it.format }
      "boolean"    | { it.type == 'boolean' && !it.format }
      "byte"       | { it.type == 'string' && it.format == 'byte' }
      "UUID"       | { it.type == 'string' && it.format == 'uuid' }
      "date"       | { it.type == 'string' && it.format == 'date' }
      "date-time"  | { it.type == 'string' && it.format == 'date-time' }
      "pet"        | { it.'type' == 'pet' }
      "List[__file]"| { it.type == 'array' && it.items.'type' == 'File' && it.uniqueItems == null }
      "Set[Pet]"   | { it.type == 'array' && it.items.'type' == 'Pet' && it.uniqueItems == true }
      "List[Pet]"  | { it.type == 'array' && it.items.'type' == 'Pet' && it.uniqueItems == null }
      "List[int]"  | { it.type == 'array' && it.items.format == 'int32' && it.items.type == 'integer' }
  }
}
