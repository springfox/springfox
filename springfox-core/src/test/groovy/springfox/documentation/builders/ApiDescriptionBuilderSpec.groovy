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

import spock.lang.Specification
import springfox.documentation.service.Operation

import java.util.function.Function

class ApiDescriptionBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
    def orderingMock = Mock(Comparator)
    def sut = new ApiDescriptionBuilder(orderingMock)
    and:
    orderingMock.sortedCopy(value) >> value
    when:
    sut."$builderMethod"(value)
    and:
    def built = sut.build()
    then:
    if (builderMethod.equals("pathDecorator")) {
      assert built.path == ""
    } else {
      built."$property" == value
    }

    where:
    builderMethod   | value             | property
    'path'          | 'urn:some-path'   | 'path'
    'description'   | 'description'     | 'description'
    'summary'       | 'summary'         | 'summary'
    'operations'    | [Mock(Operation)] | 'operations'
    'hidden'        | true              | 'hidden'
    'pathDecorator' | mock()            | 'path'
  }

  def "Setting properties on the builder with null values preserves previous value"() {
    given:
    def orderingMock = Mock(Comparator)
    def sut = new ApiDescriptionBuilder(orderingMock)

    and:
    orderingMock.sortedCopy(value) >> value

    when:
    sut."$builderMethod"(value)

    and:
    sut."$builderMethod"(null)

    and:
    def built = sut.build()

    then:
    if (builderMethod.equals("pathDecorator")) {
      assert built.path == ""
    } else {
      built."$property" == value
    }

    where:
    builderMethod   | value             | property
    'path'          | 'urn:some-path'   | 'path'
    'description'   | 'description'     | 'description'
    'operations'    | [Mock(Operation)] | 'operations'
    'pathDecorator' | mock()            | 'path'
  }

  Function<String, String> mock() {
    new Function<String, String>() {
      @Override
      String apply(String input) {
        ""
      }
    }
  }
}
