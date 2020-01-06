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

package springfox.documentation.service.model

import spock.lang.Specification
import springfox.documentation.service.AllowableRangeValues

class AllowableRangeValuesSpec extends Specification {
  def "Bean properties test"() {
    given:
    def sut = new AllowableRangeValues("0", true, "2", false)

    expect:
    sut.min == "0"
    sut.max == "2"
    sut.exclusiveMin == true
    sut.exclusiveMax == false
  }

  def "Class .equals() and .hashCode() test"() {
    given:
    def sut = new AllowableRangeValues("0", true, "2", false)
    def sutTest = new AllowableRangeValues(min, exclusiveMin, max, exclusiveMax)

    expect:
    sut.equals(sutTest) == expectedEquality
    sut.equals(sut)
    !sut.equals(null)
    !sut.equals(new Object())

    and:
    (sut.hashCode() == sutTest.hashCode()) == expectedEquality
    sut.hashCode() == sut.hashCode()

    where:
    min | exclusiveMin | max | exclusiveMax | expectedEquality
    "0" | true         | "2" | false        | true
    "1" | null         | "2" | null         | false
    "0" | true         | "3" | false        | false
    "0" | false        | "2" | false        | false
    "0" | true         | "2" | true         | false
  }

  def "Bean properties with exclusive test"() {
    given:
    def sut = new AllowableRangeValues("0", true, "2", false)
    expect:
    sut.min == "0"
    sut.exclusiveMin
    sut.max == "2"
    !sut.exclusiveMax
  }

}
