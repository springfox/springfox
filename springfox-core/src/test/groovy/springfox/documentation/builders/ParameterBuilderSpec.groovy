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

import com.fasterxml.classmate.ResolvedType
import com.google.common.base.Optional
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.AllowableListValues

class ParameterBulderSpec extends Specification {
  @Unroll
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      if (built."$property" instanceof Optional) {
        assert built."$property" == Optional.of(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod     | value                               | property
      'name'            | 'param1'                            | 'name'
      'description'     | 'param1 desc'                       | 'description'
      'defaultValue'    | 'default'                           | 'defaultValue'
      'modelRef'        | new ModelRef('string')              | 'modelRef'
      'parameterType'   | 'string'                            | 'paramType'
      'parameterAccess' | 'public'                            | 'paramAccess'
      'allowMultiple'   | true                                | 'allowMultiple'
      'required'        | true                                | 'required'
      'allowableValues' | new AllowableListValues([], "LIST") | 'allowableValues'
      'type'            | Mock(ResolvedType)                  | 'type'
  }

  @Unroll
  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      if (built."$property" instanceof Optional) {
        assert built."$property" == Optional.of(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod     | value                               | property
      'name'            | 'param1'                            | 'name'
      'description'     | 'param1 desc'                       | 'description'
      'defaultValue'    | 'default'                           | 'defaultValue'
      'modelRef'        | new ModelRef('string')              | 'modelRef'
      'parameterType'   | 'string'                            | 'paramType'
      'parameterAccess' | 'public'                            | 'paramAccess'
      'allowableValues' | new AllowableListValues([], "LIST") | 'allowableValues'
      'type'            | Mock(ResolvedType)                  | 'type'
  }
}
