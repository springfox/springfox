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

package springfox.documentation.service.model

import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.service.ResourceGroup

import static java.util.Optional.*

class ResourceGroupSpec extends Specification {
  @Shared
  def reference = new ResourceGroup("group", String) //Doesnt matter what the controller class is!

  def "Equals"() {
    expect:
      first.equals(second) == expected
    where:
      first                              | second                             | expected
      new ResourceGroup("group", String) | null                               | false
      new ResourceGroup("group", String) | new ResourceGroup(null, String)    | false
      new ResourceGroup("group", String) | new ResourceGroup(null, Integer)   | false
      new ResourceGroup("group", String) | new ResourceGroup("group", String) | true
      reference                          | reference                          | true
      new ResourceGroup("group", String) | "group"                            | false
  }

  def "Hashcode"() {
    expect:
      first.hashCode().equals(second?.hashCode()) == expected
      first.toString().equals(second?.toString()) == expected
    where:
      first                              | second                             | expected
      new ResourceGroup("group", String) | null                               | false
      new ResourceGroup("group", String) | new ResourceGroup(null, String)    | false
      new ResourceGroup("group", String) | new ResourceGroup(null, Integer)   | false
      new ResourceGroup("group", String) | new ResourceGroup("group", String) | true
      reference                          | reference                          | true
  }

  def "Bean properties work as expected without position constructor"() {
    when:
      def group = new ResourceGroup("group", String)
    then:
      group.controllerClass == ofNullable(String)
      group.groupName == "group"
      group.position == 0
  }

  def "Bean properties work as expected with position constructor"() {
    when:
      def group = new ResourceGroup("group", String, 1)
    then:
      group.controllerClass == ofNullable(String)
      group.groupName == "group"
      group.position == 1
  }
}
