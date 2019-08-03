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
import springfox.documentation.service.VendorExtension
import springfox.documentation.service.ListVendorExtension
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension

class VendorExtensionsMapperSpec extends Specification {
  class Person {
    String name;
    int age;
  }
  def second() {
    def second = new ObjectVendorExtension("x-test2")
    second.with {
      addProperty(new StringVendorExtension("x-name2", "value2"))
    }
    second
  }

  def first() {
    def first = new ObjectVendorExtension("")
    first.with {
      addProperty(new StringVendorExtension("x-test1", "value1"))
    }
    first
  }

  def third() {
    new ListVendorExtension("x-test3", [1, 3])
  }

  def fourth() {
    new VendorExtension() {
      def getValue() {
        Person person = new Person()
        person.name = "test"
        person.age = 1
        return person
      }
      String getName() {
        "x-test4"
      }
    }
  }

  def "mapper works as expected" () {
    given:
      VendorExtensionsMapper sut = new VendorExtensionsMapper()
    when:
      def mapped = sut.mapExtensions([first(), second(), third(), fourth()])
    then:
      mapped.containsKey("x-test1")
      mapped["x-test1"] == "value1"
    and:
      mapped.containsKey("x-test2")
      mapped["x-test2"] == ["x-name2": "value2"]
    and:
      mapped.containsKey("x-test3")
      mapped["x-test3"] == [1, 3]
    and:
      mapped.containsKey("x-test4")
      Person p = mapped["x-test4"]
      p.name == "test"
      p.age == 1
  }
}
