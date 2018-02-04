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

package springfox.documentation.service

import spock.lang.Specification

class TagSpec extends Specification {
  Tag tag = new Tag("pet", "Pet tag")
  
  def "should pass coverage"() {
    expect:
      tag.with {
        getName()
        getDescription()
        getOrder()
      }
  }

  def "equals works as expected"() {
    given:
      def tag1 = new Tag("Tag1", "Desc 1")
      def tag2 = new Tag("Tag2", "Desc 2")
      def tag3 = new Tag("Tag1", "Desc 1")
      def tag4 = new Tag("Tag1", "Desc 2")
      def tag5 = new Tag("Tag2", "Desc 1")
    expect:
      tag1 == tag3
      tag1 != tag4
      tag2 != tag5
      !tag1.equals("")
      !tag1.equals(tag2)
      tag1.equals(tag3)
      tag1.equals(tag1)
  }

  def "hashcode works as expected"() {
    given:
      def tag1 = new Tag("Tag1", "Desc 1")
      def tag2 = new Tag("Tag2", "Desc 2")
      def tag3 = new Tag("Tag1", "Desc 1")
    expect:
      tag1.hashCode() == tag3.hashCode()
      tag2.hashCode() != tag3.hashCode()
  }
}
