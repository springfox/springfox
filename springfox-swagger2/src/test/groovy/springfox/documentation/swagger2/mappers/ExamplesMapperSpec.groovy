/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

import spock.lang.Specification
import springfox.documentation.schema.Example

class ExamplesMapperSpec extends Specification {
  def "examples are mapped correctly"() {
    given:
    def mediaType = "mediaType"
    def value = "value"
    def examples = new ArrayList<>()
    examples.add(new Example(mediaType, value))

    when:
    def sut = new ExamplesMapper()

    then:
    def mapped = sut.mapExamples(examples)

    and:
    mapped.size() == 1
    mapped.get(mediaType) == value
  }

  def "null mediaType is converted to empty string"() {
    given:
    def mediaType = null
    def value = "value"
    def examples = new ArrayList<>()
    examples.add(new Example(mediaType, value))

    when:
    def sut = new ExamplesMapper()

    then:
    def mapped = sut.mapExamples(examples)

    and:
    mapped.size() == 1
    mapped.get("") == value
  }

  def "empty example list maps to empty map"() {
    given:
    def examples = new ArrayList<>()

    when:
    def sut = new ExamplesMapper()

    then:
    def mapped = sut.mapExamples(examples)

    and:
    mapped.size() == 0
  }
}
