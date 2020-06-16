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

import spock.lang.Specification
import springfox.documentation.schema.Example
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.Header


class ResponseMessageBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
    def sut = new ResponseMessageBuilder()

    when:
    sut."$builderMethod"(value)

    and:
    def built = sut.build()

    then:
    built."$property" == value

    where:
    builderMethod   | value                               | property
    'code'          | 200                                 | 'code'
    'message'       | 'OK'                                | 'message'
    'responseModel' | new ModelRef('String')              | 'responseModel'
    'examples'      | [new Example("mediaType", "value")] | 'examples'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
    def sut = new ResponseMessageBuilder()

    when:
    sut."$builderMethod"(value)
    sut."$builderMethod"(null)

    and:
    def built = sut.build()

    then:
    built."$property" == value

    where:
    builderMethod   | value                               | property
    'message'       | 'OK'                                | 'message'
    'responseModel' | new ModelRef('String')              | 'responseModel'
    'examples'      | [new Example("mediaType", "value")] | 'examples'
  }


  def "Deprecated headers transforms into headers with no description"() {
    given:
    def sut = new ResponseMessageBuilder()

    when:
    sut.headersWithDescription(headersWithEmptyDescription("header1", "header2"))
    sut.headersWithDescription(headersWithEmptyDescription("header3"))
    sut.headersWithDescription(null)

    and:
    def built = sut.build()
    def expected = headersWithEmptyDescription("header1", "header2")
    expected.putAll(headersWithDescription("header3"))

    then:
    expected.entrySet().each({
      assert built.headers.containsKey(it.key)
      assert built.headers.get(it.key).name == expected.get(it.key).name
      assert built.headers.get(it.key).description == expected.get(it.key).description
      assert built.headers.get(it.key).modelReference.type == expected.get(it.key).modelReference.type
    })

  }

  def headers(String... names) {
    def map = new HashMap<>()
    names.collect({ map.put(it, new ModelRef("string")) })
    map
  }

  def headersWithEmptyDescription(String... names) {
    headersWithDescription("", names)
  }

  def headersWithDescription(String description, String... names) {
    def map = new HashMap<>()
    names.collect({ map.put(it, new Header(
        it,
        description,
        new ModelRef("string"),
        null
    )) })
    map
  }
}
