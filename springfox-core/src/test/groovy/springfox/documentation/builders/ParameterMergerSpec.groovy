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
package springfox.documentation.builders

import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.core.Ordered.*

class ParameterMergerSpec extends Specification {
  @Unroll
  def "Merges parameters by name"() {
    given:
    def merger = new ParameterMerger(destination, source)

    when:
    def merged = merger.merged()
    def expected = new HashSet<>()
    expected.addAll(destination.collect { it.name })
    expected.addAll(source.collect { it.name })

    then:
    merged.size() == expected.size()

    where:
    destination          | source
    [param("a", "desc")] | [param("a", "desc2")]
    [param("a", "desc")] | [param("b", "desc2")]
    [param("a", "desc")] | []
    []                   | [param("a", "desc")]
    []                   | []
  }

  @Unroll
  def "Merge prioritizes the merges by order"() {
    given:
    def merger = new ParameterMerger([first], [second])

    when:
    def merged = merger.merged()


    then:
    merged.size() == 1
    merged.first().description == "winning desc"

    where:
    first                                   | second
    param("a", "desc")                      | param("a", "winning desc")
    param("a", "desc", LOWEST_PRECEDENCE)   | param("a", "winning desc", 0)
    param("a", "desc", 0)                   | param("a", "winning desc", HIGHEST_PRECEDENCE)
    param("a", "winning desc", HIGHEST_PRECEDENCE) | param("a", "desc", 0)

  }

  def param(String name, String desc, order = LOWEST_PRECEDENCE) {
    new ParameterBuilder()
        .name(name)
        .description(desc)
        .order(order)
        .build()
  }
}
