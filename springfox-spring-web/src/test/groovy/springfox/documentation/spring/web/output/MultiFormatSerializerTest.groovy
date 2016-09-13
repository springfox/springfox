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

package springfox.documentation.spring.web.output

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springfox.documentation.spring.web.output.JacksonModuleRegistrar
import springfox.documentation.spring.web.output.MultiFormatSerializer

class MultiFormatSerializerTest extends Specification {
  def "should serialize"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([])
      def objectMapper = Mock(ObjectMapper)
      sut.mappers.put("json", objectMapper)
      String object = 'a string'
    when:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
  }

  def "should fallback to json"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([])
      def jsonMapper = Mock(ObjectMapper)
      sut.mappers.put("json", jsonMapper)
      String object = 'a string'
    when:
      sut.serialize(object, "xml")
    then:
      1 * jsonMapper.writeValueAsString(object)
  }

  def "should serialize with custom registrars"() {
    given: "mocks"
      def registrar = Mock(JacksonModuleRegistrar)
      def jsonMapper = Mock(ObjectMapper)
      def yamlMapper = Mock(ObjectMapper)
      def object = 'a string'
    when:
      MultiFormatSerializer sut = new MultiFormatSerializer([registrar])
      sut.mappers.put("yaml", yamlMapper)
      sut.mappers.put("json", jsonMapper)
    and:
      sut.serialize(object, "yaml")
    then:
      1 * yamlMapper.writeValueAsString(object)
      0 * jsonMapper.writeValueAsString(object)
      2 * registrar.maybeRegisterModule(_)
  }
}
