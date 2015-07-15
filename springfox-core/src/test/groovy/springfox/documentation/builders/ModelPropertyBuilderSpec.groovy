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
import springfox.documentation.service.AllowableListValues

class ModelPropertyBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value                                 | property
      'position'          | 1                                     | 'position'
      'name'              | 'model1'                              | 'name'
      'type'              | new TypeResolver().resolve(String)    | 'type'
      'qualifiedType'     | 'com.Model1'                          | 'qualifiedType'
      'description'       | 'model1 desc'                         | 'description'
      'required'          | true                                  | 'required'
      'readOnly'          | true                                  | 'readOnly'
      'isHidden'          | true                                  | 'hidden'
      'allowableValues'   | new AllowableListValues(['a'], "LIST")| 'allowableValues'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'name'              | 'model1'                              | 'name'
      'type'              | new TypeResolver().resolve(String)    | 'type'
      'qualifiedType'     | 'com.Model1'                          | 'qualifiedType'
      'description'       | 'model1 desc'                         | 'description'
      'allowableValues'   | new AllowableListValues(['a'], "LIST")| 'allowableValues'
  }

  def "When allowable list value is empty builder sets the value to null"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut.allowableValues(new AllowableListValues([], "LIST"))
    and:
      def built = sut.build()
    then:
      built.allowableValues == null
  }
}
