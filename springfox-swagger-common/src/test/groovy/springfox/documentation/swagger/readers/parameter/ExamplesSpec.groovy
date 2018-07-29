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
package springfox.documentation.swagger.readers.parameter

import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import spock.lang.Specification


class ExamplesSpec extends Specification {
  def "cannot instantiate"() {
    when:
    new Examples()

    then:
    thrown(UnsupportedOperationException)
  }

  def "renders examples annotations"() {
    when:
    def examples = Examples.examples(examples())

    then:
    examples.size() == 1
    examples.containsKey("application/json")
    examples.get("application/json").size() == 1

  }

  def "when example is blank it is ignored"() {
    when:
    def examples = Examples.examples(examplesWithBlankValue())

    then:
    examples.size() == 0
  }

  def "renders examples annotations with blank media type"() {
    when:
    def examples = Examples.examples(examplesWithBlankMediaType())

    then:
    examples.size() == 1
    examples.containsKey("")
    examples.get("").size() == 1
    !examples.get("").first().mediaType.isPresent()
    examples.get("").first().value == "{'hello': 'world'}"
  }

  def "renders examples annotations with null media type"() {
    when:
    def examples = Examples.examples(examplesWithNullMediaType())

    then:
    examples.size() == 1
    examples.containsKey(null)
    examples.get(null).size() == 1
    !examples.get(null).first().mediaType.isPresent()
    examples.get(null).first().value == "{'hello': 'world'}"
  }

  Example examples() {
    [
        value: { ->
          [
              [
                  mediaType: { -> "application/json" },
                  value   : { -> "{'hello': 'world'}" }
              ]
          ] as ExampleProperty[]
        }
    ] as Example
  }

  Example examplesWithBlankValue() {
    [
        value: { ->
          [
              [
                  mediaType: { -> "application/json" },
                  value   : { -> "" }
              ]
          ] as ExampleProperty[]
        }
    ] as Example
  }

  Example examplesWithBlankMediaType() {
    [
        value: { ->
          [
              [
                  mediaType: { -> "" },
                  value   : { -> "{'hello': 'world'}" }
              ]
          ] as ExampleProperty[]
        }
    ] as Example
  }

  Example examplesWithNullMediaType() {
    [
        value: { ->
          [
              [
                  mediaType: { -> null },
                  value   : { -> "{'hello': 'world'}" }
              ]
          ] as ExampleProperty[]
        }
    ] as Example
  }
}
