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

package springfox.documentation.schema.property.bean

import spock.lang.Specification
import springfox.documentation.schema.TypeForTestingPropertyNames
import springfox.documentation.schema.TypeWithGettersAndSetters

import static springfox.documentation.schema.property.bean.Accessors.*

class AccessorsSpec extends Specification {
  def "Cannot instantiate the Accessors helper"() {
    when:
      new Accessors()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Property names are identified correctly based on (get/set) method names"() {
    given:
      def sut = TypeForTestingPropertyNames
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      propertyName(method) == property

    where:
      methodName       || property
      "getProp"        || "prop"
      "getProp1"       || "prop1"
      "getProp_1"      || "prop_1"
      "isProp"         || "prop"
      "isProp1"        || "prop1"
      "isProp_1"       || "prop_1"
      "setProp"        || "prop"
      "setProp1"       || "prop1"
      "setProp_1"      || "prop_1"
      "prop"           || ""
      "getAnotherProp" || "prop"
      "setAnotherProp" || "prop"
      "getPropFallback"|| "propFallback"
      "setPropFallback"|| "propFallback"
  }

  def "Identifies JsonSetter annotation"() {
    given:
      def sut = TypeForTestingPropertyNames

    when:
      def method = sut.methods.find { it.name.equals(methodName) }

    then:
      isSetter(method)

    where:
      methodName << ["setAnotherProp", "anotherProp"]
  }

  def "Identifies JsonGetter annotation"() {
    given:
      def sut = TypeForTestingPropertyNames

    when:
      def method = sut.methods.find { it.name.equals(methodName) }

    then:
      maybeAGetter(method)

    where:
      methodName << ["yetAnotherProp", "getAnotherProp"]
  }

  def "Getters (#methodName) are identified correctly"() {
    given:
      def sut = TypeWithGettersAndSetters
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      maybeAGetter(method) == result

    where:
      methodName      || result
      "getIntProp"    || true
      "setIntProp"    || false
      "isBoolProp"    || true
      "setBoolProp"   || false
      "getVoid"       || false
      "isNotGetter"   || true
      "getWithParam"  || false
      "setNotASetter" || true
  }

  def "Setters are identified correctly"() {
    given:
      def sut = TypeWithGettersAndSetters
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      isSetter(method) == result

    where:
      methodName      || result
      "getIntProp"    || false
      "setIntProp"    || true
      "isBoolProp"    || false
      "setBoolProp"   || true
      "getVoid"       || false
      "isNotGetter"   || false
      "getWithParam"  || false
      "setNotASetter" || false
  }
}
