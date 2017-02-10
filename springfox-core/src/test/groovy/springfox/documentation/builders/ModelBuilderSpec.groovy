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

package springfox.documentation.builders

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import springfox.documentation.schema.ModelProperty

class ModelBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ModelBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'id'            | 'model1'                              | 'id'
      'name'          | 'model1'                              | 'name'
      'index'         | 0                                     | 'index'
      'qualifiedType' | 'com.Model1'                          | 'qualifiedType'
      'description'   | 'model1 desc'                         | 'description'
      'baseModel'     | 'baseModel1'                          | 'baseModel'
      'discriminator' | 'decriminator'                        | 'discriminator'
      'type'          | new TypeResolver().resolve(String)    | 'type'
      'isMapType'     | false                                 | 'map'
      'subTypes'      | ["String"]                            | 'subTypes'
      'properties'    | [p1: Mock(ModelProperty)]             | 'properties'
      'example'       | 'example1'                            | 'example'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ModelBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'id'            | 'model1'                              | 'id'
      'name'          | 'model1'                              | 'name'
      'index'         | 0                                     | 'index'
      'qualifiedType' | 'com.Model1'                          | 'qualifiedType'
      'description'   | 'model1 desc'                         | 'description'
      'baseModel'     | 'baseModel1'                          | 'baseModel'
      'discriminator' | 'decriminator'                        | 'discriminator'
      'type'          | new TypeResolver().resolve(String)    | 'type'
      'isMapType'     | false                                 | 'map'
      'subTypes'      | ["String"]                            | 'subTypes'
      'properties'    | [p1: Mock(ModelProperty)]             | 'properties'
      'example'       | 'example1'                            | 'example'
  }
  
  def "Setting index propertie change .id() and .name() methods"() {
    given:
      def sut = new ModelBuilder()
    when:
      sut.id = id
      sut.name = name
    and:
      sut.index = index
    
      def built = sut.build()
    then:
      sut.build().getId() == expId
      sut.build().getName() == expName

    where:
      id     | name   | index | expId    | expName 
      "Test" | "Test" | 0     | "Test"   | "Test"
      "Test" | "Test" | null  | "Test"   | "Test"
      "Test" | "Test" | 1     | "Test_1" | "Test_1"
  }
}
