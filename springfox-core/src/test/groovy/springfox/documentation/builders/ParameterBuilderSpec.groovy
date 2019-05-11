/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.AllowableListValues

import static java.util.Optional.*

class ParameterBuilderSpec extends Specification {
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
      assert built."$property" == of(value)
    } else {
      assert built."$property" == value
    }

    where:
    builderMethod     | value                                  | property
    'name'            | 'param1'                               | 'name'
    'description'     | 'param1 desc'                          | 'description'
    'defaultValue'    | 'default'                              | 'defaultValue'
    'modelRef'        | new ModelRef('string')                 | 'modelRef'
    'parameterType'   | 'query'                                | 'paramType'
    'parameterAccess' | 'public'                               | 'paramAccess'
    'pattern'         | '[a-zA-Z0-9_]'                         | 'pattern'
    'allowMultiple'   | true                                   | 'allowMultiple'
    'required'        | true                                   | 'required'
    'allowableValues' | new AllowableListValues(['a'], "LIST") | 'allowableValues'
    'type'            | Mock(ResolvedType)                     | 'type'
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
      assert built."$property" == of(value)
    } else {
      assert built."$property" == value
    }

    where:
    builderMethod     | value                                  | property
    'name'            | 'param1'                               | 'name'
    'description'     | 'param1 desc'                          | 'description'
    'defaultValue'    | 'default'                              | 'defaultValue'
    'modelRef'        | new ModelRef('string')                 | 'modelRef'
    'parameterType'   | 'query'                                | 'paramType'
    'parameterAccess' | 'public'                               | 'paramAccess'
    'pattern'         | '[a-zA-Z0-9_]'                         | 'pattern'
    'allowableValues' | new AllowableListValues(['a'], "LIST") | 'allowableValues'
    'type'            | Mock(ResolvedType)                     | 'type'
  }

  def "Setting builder allowableValue to empty or null values preserves existing values"() {
    given:
    def sut = new ParameterBuilder()

    when:
    sut.allowableValues(currentValue)
    sut.allowableValues(newValue)

    and:
    def built = sut.build()

    then:
    built.allowableValues == currentValue

    where:
    newValue                            | currentValue
    new AllowableListValues([], "LIST") | new AllowableListValues(['a'], "LIST")
    null                                | new AllowableListValues(['a'], "LIST")
  }

  @Unroll
  def "Setting builder allowEmptyValue to #allowEmptyValue when parameter type is #parameterType"() {
    given:
    def sut = new ParameterBuilder()

    when:
    sut.allowEmptyValue(allowEmptyValue)
    sut.parameterType(parameterType)

    and:
    def built = sut.build()

    then:
    built.allowEmptyValue == expectedAllowEmptyValue

    where:
    parameterType | allowEmptyValue | expectedAllowEmptyValue
    "query"       | true            | true
    "query"       | false           | false
    "query"       | null            | null
    "formData"    | true            | true
    "formData"    | false           | false
    "formData"    | null            | null
    "form"        | true            | null
    "form"        | false           | null
    "form"        | null            | null
    "header"      | true            | null
    "header"      | false           | null
    "header"      | null            | null
    "cookie"      | true            | null
    "cookie"      | false           | null
    "cookie"      | null            | null
    "path"        | true            | null
    "path"        | false           | null
    "path"        | null            | null
    "body"        | true            | null
    "body"        | false           | null
    "body"        | null            | null

  }
}
