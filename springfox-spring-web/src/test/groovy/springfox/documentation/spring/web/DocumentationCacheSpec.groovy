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

package springfox.documentation.spring.web

import spock.lang.Specification
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.spring.web.DocumentationCache

class DocumentationCacheSpec extends Specification {
  def "Behaves like a map" () {
    given:
      def sut = new DocumentationCache()
    and:
      sut.addDocumentation(new DocumentationBuilder().name("test").build())

    when:
      def group = sut.documentationByGroup("test")
    then:
      group != null
      group.groupName == "test"
    and:
      sut.documentationByGroup("non-existent") == null
  }

  def "Cache can be cleared " () {
    given:
      def sut = new DocumentationCache()
    and:
      sut.addDocumentation(new DocumentationBuilder().name("test").build())
    when:
      def group = sut.documentationByGroup("test")
    then:
      group != null
      group.groupName == "test"
    and:
      sut.clear()
      sut.documentationByGroup("test") == null
  }

}
