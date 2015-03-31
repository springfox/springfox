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

package springfox.documentation.builders

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDate
import spock.lang.Specification

import static BuilderDefaults.*

class BuilderDefaultsSpec extends Specification {
  def "BuilderDefaults is a static class" () {
    when:
      new BuilderDefaults()
    then:
      thrown(UnsupportedOperationException)
  }

  def "defaultIfAbsent returns default value if newValue is null" () {
    expect:
      defaultIfAbsent('newValue', 'oldValue') == 'newValue'
      defaultIfAbsent('newValue', null) == 'newValue'
      defaultIfAbsent(null, 'oldValue') == 'oldValue'
      defaultIfAbsent(null, null) == null
  }

  def "replaceIfMoreSpecific returns default value if newValue is null or resolves to Object" () {
    given:
      def resolver = new TypeResolver()
    expect:
      replaceIfMoreSpecific(resolver.resolve(String), resolver.resolve(LocalDate)).erasedType == String
      replaceIfMoreSpecific(resolver.resolve(Object), resolver.resolve(LocalDate)).erasedType == LocalDate
      replaceIfMoreSpecific(resolver.resolve(String), null).erasedType == String
      replaceIfMoreSpecific(resolver.resolve(Object), null).erasedType == Object
      replaceIfMoreSpecific(resolver.resolve(LocalDate), resolver.resolve(Object)).erasedType == LocalDate
      replaceIfMoreSpecific(null, resolver.resolve(Object)).erasedType == Object
      replaceIfMoreSpecific(null, null) == null
  }

  def "nullToEmptyList transforms null values to empty list" () {
    expect:
      nullToEmptyList([]).size() == 0
      nullToEmptyList(['string']).size() == 1
      List nullList = null
      nullToEmptyList(nullList).size() == 0
  }
}
