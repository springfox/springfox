/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import spock.lang.Specification

import static springfox.documentation.schema.Collections.*

class CollectionsSpec extends Specification {
  def "Container type throws exception when its passed a non-iterable type"() {
    when:
      Collections.containerType(new TypeResolver().resolve(ExampleEnum))
    then:
      thrown(UnsupportedOperationException)
  }

  def "Container element type is null when its passed a non-iterable type"() {
    when:
      def type  = collectionElementType(new TypeResolver().resolve(ExampleEnum))
    then:
      type == null
  }

  def "Container element type is null when its passed a Map"() {
    when:
    def type  = collectionElementType(new TypeResolver().resolve(Map))
    then:
    type == null
  }

  def "Container element type is Object when its passed a Collection<Object>"() {
    when:
    def type  = collectionElementType(new TypeResolver().resolve(Collection))
    then:
    type instanceof Object
  }

}
