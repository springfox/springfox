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
package springfox.documentation.spring.web.plugins

import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.DocumentationPlugin

import java.util.stream.Stream

import static java.util.Collections.*
import static java.util.stream.Collectors.*
import static springfox.documentation.spring.web.plugins.DuplicateGroupsDetector.*

class DuplicateGroupsDetectorSpec extends Specification {
  def "The DuplicateGroupsDetector cannot be instantiated"() {
    when:
      new DuplicateGroupsDetector()
    then:
      thrown(UnsupportedOperationException)
  }

  def "The plugin list is empty"() {
    when:
      def plugins = new ArrayList<>()
    and:
      ensureNoDuplicateGroups(plugins)
    then:
      noExceptionThrown()
  }

  def "The plugin list has one element"() {
    given:
      def plugin1 = Mock(DocumentationPlugin)
      def plugins = singletonList(plugin1)
    and:
      plugin1.getGroupName() >> "group1"
    when:
      ensureNoDuplicateGroups(plugins)
    then:
      noExceptionThrown()
  }

  def "The plugin list has multiple unique elements"() {
    given:
      def plugin1 = Mock(DocumentationPlugin)
      def plugin2 = Mock(DocumentationPlugin)
      def plugins = Stream.of(plugin1, plugin2).collect(toList())
    and:
      plugin1.getGroupName() >> "group1"
      plugin2.getGroupName() >> "group2"
    when:
      ensureNoDuplicateGroups(plugins)
    then:
      noExceptionThrown()
  }

  def "The plugin list has duplicate elements"() {
    given:
      def plugin1 = Mock(DocumentationPlugin)
      def plugin2 = Mock(DocumentationPlugin)
      def plugins = Stream.of(plugin1, plugin2).collect(toList())
    and:
      plugin1.getGroupName() >> "group1"
      plugin2.getGroupName() >> "group1"
    when:
      ensureNoDuplicateGroups(plugins)
    then:
      thrown(IllegalStateException)
  }

  def "A plugin with a null groupName is not considered a duplicate"() {
    def plugins = [new Docket(DocumentationType.SWAGGER_2)]
    expect:
      ensureNoDuplicateGroups(plugins)
  }
}
