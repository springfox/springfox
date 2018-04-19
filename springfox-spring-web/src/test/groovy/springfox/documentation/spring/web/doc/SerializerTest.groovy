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

package springfox.documentation.spring.web.doc

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class SerializerTest extends Specification {

  def "should serialize"() {
    given:
      def objectMapper = Mock(ObjectMapper)
      def jsonSerializer = new JsonFormatSerializer(objectMapper);
      Serializer sut = new Serializer([jsonSerializer], [])
      String object = 'a string'
    when:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
  }

  def "should identify invalid formats"() {
    given:
      Serializer sut = new Serializer([], [])
      String object = 'a string'
    when:
      sut.serialize(object, "falafel")
    then:
      def throwable = thrown(IllegalArgumentException)
      "No serializer registered for falafel" == throwable.message
  }

  def "should signal invalid types"() {
    given:
      Serializer sut = new Serializer([], [])
    expect:
      !sut.supports("falafel")
  }

  def "should serialize with custom registrars"() {
    given: "mocks"
      def registrar = Mock(JacksonModuleRegistrar)
      def objectMapper = Mock(ObjectMapper)
      def jsonSerializer = new JsonFormatSerializer(objectMapper);
      def object = 'a string'
    when:
      Serializer sut = new Serializer([jsonSerializer], [registrar])
    and:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
      1 * registrar.maybeRegisterModule(_)
  }

}
