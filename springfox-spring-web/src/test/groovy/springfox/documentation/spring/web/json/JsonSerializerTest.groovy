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

package springfox.documentation.spring.web.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class JsonSerializerTest extends Specification {
  def "should serialize"() {
    given:
      JsonSerializer sut = new JsonSerializer([])
      def objectMapper = Mock(ObjectMapper)
      sut.objectMapper = objectMapper
      String object = 'a string'
    when:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
  }

  def "should serialize with custom registrars"() {
    given: "mocks"
      def registrar = Mock(JacksonModuleRegistrar)
      def objectMapper = Mock(ObjectMapper)
      def object = 'a string'
    when:
      JsonSerializer sut = new JsonSerializer([registrar])
      sut.objectMapper = objectMapper
    and:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
      1 * registrar.maybeRegisterModule(_)
  }
}
