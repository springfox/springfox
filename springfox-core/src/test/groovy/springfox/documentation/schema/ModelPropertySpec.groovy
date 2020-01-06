/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.AllowableRangeValues

class ModelPropertySpec extends Specification {
  @Shared
  def resolver = new TypeResolver()

  @Unroll
  def "ModelProperty .equals and .hashCode works as expected"() {
    given:
    def property = property("Test")

    def testProperty = new ModelProperty(
        name,
        resolver.resolve(type),
        qualifiedType,
        position,
        required,
        isHidden,
        readOnly,
        allowEmptyValue,
        description,
        allowable,
        example,
        pattern,
        defaultValue,
        new Xml(),
        new ArrayList<>())

    expect:
    property.equals(testProperty) == expectedEquality
    property.equals(property)
    !property.equals(null)
    !property.equals(new Object())

    and:
    (property.hashCode() == testProperty.hashCode()) == expectedEquality
    property.hashCode() == property.hashCode()

    where:
    name    | type    | qualifiedType | position | required | isHidden | readOnly | allowEmptyValue | description | allowable  | modelRef | example    | pattern    | defaultValue | expectedEquality
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | "default"           | true
    "Test1" | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | Integer | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT2"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 5        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | false    | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | false    | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | false    | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | false           | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc1"     | allow("3") | ref()    | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref("T") | "example"  | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example1" | "pattern"  | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern1" | ""           | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | "def"        | false
    "Test"  | String  | "qT1"         | 0        | true     | true     | true     | true            | "desc"      | allow()    | ref()    | "example"  | "pattern"  | ""           | false
  }

  def allow(min = "1", max = "5") {
    new AllowableRangeValues(min, max)
  }

  def ref(type = "string", model = null, isMap = true) {
    new ModelRef(type, model, isMap)
  }

  def property(String name) {
    new ModelProperty(
        name,
        resolver.resolve(String),
        "qT1",
        0,
        true,
        true,
        true,
        true,
        "desc",
        new AllowableRangeValues("1", "5"),
        "example",
        "pattern",
        "default",
        new Xml(),
        new ArrayList<>())
  }
}
