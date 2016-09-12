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

package springfox.documentation.schema

import spock.lang.Specification
import springfox.documentation.service.AllowableListValues

import static com.google.common.collect.Lists.*

class EnumsSpec extends Specification {
  def "enums support @JsonValue annotation"() {
    given:
      def expected = new AllowableListValues(newArrayList("One", "Two"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(JsonValuedEnum).getValues()

  }

  def "enums support regular enums"() {
    given:
      def expected = new AllowableListValues(newArrayList("ONE", "TWO"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(ExampleEnum).getValues()
  }

  def "enums work with incorrectly annotated enums"() {
    given:
      def expected = new AllowableListValues(newArrayList("ONE", "TWO"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(IncorrectlyJsonValuedEnum).getValues()
  }

  def "Enums class in not instantiable"() {
    when:
      new Enums()
    then:
      thrown(UnsupportedOperationException)
  }

  def "we shouldn't have duplicate enum representations"() {
    given:
    def expected = Arrays.asList("one", "two")
    expect:
    expected.equals(Enums.getEnumValues(DuplicateRepresentationEnum))
  }

}
