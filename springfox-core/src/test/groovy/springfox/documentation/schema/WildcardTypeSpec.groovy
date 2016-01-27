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

import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Type

import static WildcardType.*

class WildcardTypeSpec extends Specification {
  @Shared TypeResolver resolver = new TypeResolver()
  def "Cannot instantiate the wildcardtype class" () {
    when:
      new WildcardType()
    then:
      thrown(UnsupportedOperationException)
  }

  def "wildcard matches checks equivalence of types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
    type                               | testType                         | shouldMatch
    hashMap(String, String)            | hashMap(String, Integer)         | false
    hashMap(WildcardType, String)      | hashMap(String, Integer)         | false
    hashMap(String, WildcardType)      | hashMap(Integer, Integer)        | false
    hashMap(String, Integer)           | hashMap(String, String)          | false
    hashMap(String, Integer)           | hashMap(WildcardType, String)    | false
    hashMap(Integer, Integer)          | hashMap(String, WildcardType)    | false
    hashMap(String, String)            | hashMap(String, String)          | true
    hashMap(WildcardType, String)      | hashMap(String, String)          | true
    hashMap(WildcardType, String)      | hashMap(Integer, String)         | true
    hashMap(String, WildcardType)      | hashMap(String, String)          | true
    hashMap(String, WildcardType)      | hashMap(String, Integer)         | true
    hashMap(WildcardType, WildcardType)| hashMap(Integer, String)         | true
  }


  def "wildcard matches checks equivalence of types with nested wildcard types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
      type                               | testType                         | shouldMatch
      nestedGenericType(WildcardType)    | nestedGenericType(String)        | true
      nestedGenericType(String)          | nestedGenericType(WildcardType)  | false
      resolve(List, WildcardType)        | resolve(ArrayList, String)       | false
      resolve(List)                      | resolve(ArrayList, String)       | false
      resolve(ArrayList, String)         | resolve(List)                    | false
  }

  def "wildcard matches fail when the type bindings are not of the same size" () {
    given:
    expect:
      !wildcardMatch(testType, type)

    where:
      type                               | testType
      nestedGenericType(WildcardType)    | resolve(String)
      resolve(List, WildcardType)        | resolve(String)
  }


  def "Replacing wild cards throws an exception when the replaceables is empty" () {
    given:
      Iterable<ResolvedType> replaceables = []
    when:
      replaceWildcardsFrom(replaceables, resolver.resolve(WildcardType))
    then:
      thrown(IllegalStateException)
  }

  def "Collecting replaceables works on nested types" () {
    given:
      def wildcardtype = resolve(List, resolve(List, WildcardType))
      def replacingType = resolve(List, resolve(List, String))
    when:
      def replacables = collectReplaceables(replacingType, wildcardtype)
    then:
      replacables.size() == 1
  }

  def "Collecting replaceables works on erased types" () {
    given:
    def wildcardtype = resolve(List, WildcardType)
    def replacingType = resolve(List)
    when:
    def replacables = collectReplaceables(replacingType, wildcardtype)
    then:
    replacables.size() == 1
  }

  def "Replacing wild cards throws an exception when the number of replaceables don't match the wildcards in the type being replaced" () {
    given:
      Iterable<ResolvedType> replaceables = [resolve(String)]
    when:
      replaceWildcardsFrom(replaceables, resolver.resolve(Map, WildcardType, WildcardType))
    then:
      thrown(IllegalStateException)
  }

  ResolvedType hashMap(def keyClazz, def valueClazz) {
    resolver.resolve(Map, keyClazz, valueClazz)
  }

  ResolvedType nestedGenericType(def clazz) {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, clazz))
  }

  ResolvedType resolve(Class clazz, Type ... typeBindings) {
    resolver.resolve(clazz, typeBindings)
  }
}
