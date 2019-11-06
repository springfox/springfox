/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.service

import spock.lang.Specification


class ListVendorExtensionSpec extends Specification {
  def "List vendor extension adds and retrieves properties" () {
    given:
      ListVendorExtension sut = new ListVendorExtension<Integer>("Name", [1, 2])
    expect:
      sut.getName().equals("Name")
      sut.value.size() == 2
      sut.value == [1, 2]
  }

  def "List vendor extension works with empty lists" () {
    given:
      ListVendorExtension sut = new ListVendorExtension<Integer>("Name", [])
    expect:
      sut.getName().equals("Name")
      sut.value.size() == 0
  }

  def "List vendor extension works with null" () {
    given:
      ListVendorExtension sut = new ListVendorExtension<Integer>("Name", null)
    expect:
      sut.getName().equals("Name")
      sut.value.size() == 0
  }

  def "Class .equals() and .hashCode() test" () {
    given:
      def sut = new ListVendorExtension<Integer>("Name", null)
      def sutTest = new ListVendorExtension<Integer>(name, values)
    expect:
      sut.equals(sutTest) == expectedEquality
      sut.equals(sut)
      !sut.equals(null)
      !sut.equals(new Object())
    and:
      (sut.hashCode() == sutTest.hashCode()) == expectedEquality
      sut.hashCode() == sut.hashCode()
    where:
      name    | values                                      | expectedEquality
      "Name"  | null                                        | true
      "Name1" | null                                        | false
      "Name"  | new ArrayList<Integer>()                    | true
      "Name"  | new ArrayList<Integer>(Arrays.asList(1, 3)) | false
    }
}
