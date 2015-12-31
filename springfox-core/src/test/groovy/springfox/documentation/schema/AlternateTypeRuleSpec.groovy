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

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static AlternateTypeRules.*

class AlternateTypeRuleSpec extends Specification {
  @Shared TypeResolver resolver = new TypeResolver()

  def "Cannot instantiate the AlternateTypeRules class" () {
    when:
      new AlternateTypeRules()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Can figure out if the rule applies to a given type" () {
    given:
      def sut = newRule(original, alternate) //Alternate doesn't matter for this test
    expect:
      sut.appliesTo(testType) == isApplicable
    where:
      original                          | alternate                 | testType                      | isApplicable
      resolve(Date)                     | resolve(String)           | resolve(Date)                 | true
      resolve(Date)                     | resolve(String)           | resolve(String)               | false
      resolve(List, Date)               | resolve(String)           | resolve(List, Date)           | true
      resolve(List, Date)               | resolve(String)           | resolve(List, String)         | false
      resolve(List, WildcardType)       | resolve(String)           | resolve(List, String)         | true
      resolve(List, WildcardType)       | resolve(String)           | resolve(List, WildcardType)   | true
      listOfListsOfType(WildcardType)   | listOfListsOfType(String) | listOfListsOfType(Date)       | true
      resolve(WildcardType)             | resolve(String)           | resolve(Date)                 | false
  }

  private ResolvedType listOfListsOfType(Class clazz) {
    resolve(List, resolve(List, clazz))
  }

  @Unroll
  def "Rules provide the correct alternate types given #testType." () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(expected)
    where:
      original                    | alternate                           | testType                      | expected
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, String)         | resolve(Map, String, String)
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, Date)           | resolve(Map, String, Date)
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, WildcardType)   | resolve(Map, String, WildcardType)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, String)         | resolve(Map, String, String)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, Date)           | resolve(Map, Date, String)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, WildcardType)   | resolve(Map, WildcardType, String)
      resolve(WildcardType)       | resolve(String)                     | resolve(Date)                 | resolve(Date)
      resolve(WildcardType)       | resolve(String)                     | resolve(WildcardType)         | resolve(String)
      resolve(List, WildcardType) | resolve(String)                     | resolve(Date)                 | resolve(Date)
      resolve(List, WildcardType) | resolve(WildcardType)               | resolve(List, Date)           | resolve(Date)
  }

  def "Can figure out if Map Rules apply" () {
    given:
      def sut = newMapRule(String, original) //Alternate doesn't matter for this test
    expect:
      sut.appliesTo(resolve(Map, String, testType)) == isApplicable
    where:
      original                 | alternate        | testType             | isApplicable
      Date                     | String           | Date                 | true
      WildcardType             | String           | Date                 | true
      WildcardType             | String           | String               | true
  }

  def "When the shape of the wildcard original type and the test type dont match" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(testType)
    where:
      original                    | alternate              | testType
      resolve(List, WildcardType) | resolve(List, String)  | resolve(String)
      resolve(List, String)       | resolve(List, Date)    | resolve(List, Integer)
  }

  def "When the shape of the wildcard original type and the test type matches" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(testType)
    where:
      original                    | alternate              | testType
      resolve(List, WildcardType) | resolve(List, String)  | resolve(List, String)
  }

  ResolvedType resolve(Class clazz, Type ... typeBindings) {
    resolver.resolve(clazz, typeBindings)
  }
}
