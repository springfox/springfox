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
import springfox.documentation.spring.web.output.formats.CustomFormatOutputMapper

import static org.springframework.http.MediaType.*

class MultiFormatSerializerTest extends Specification {

  def APPLICATION_YAML = parseMediaType("application/yaml")

  def "should serialize"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
      def objectMapper = Mock(ObjectMapper)
      sut.mappers.put(APPLICATION_JSON, objectMapper)
      String object = 'a string'
    when:
      sut.toJson(object)
    then:
      1 * objectMapper.writeValueAsString(object)
  }

  def "should not serialize unavailable types"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
      def jsonMapper = Mock(ObjectMapper)
      sut.mappers.put(APPLICATION_JSON, jsonMapper)
      String object = 'a string'
    when:
      sut.serialize(object, APPLICATION_XML)
    then:
      thrown(IllegalArgumentException)
  }

  def "should throw exception on impossible type"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
      def jsonMapper = new ObjectMapper()
      sut.mappers.put(APPLICATION_JSON, jsonMapper)
      def impossibleType = Mock(Map)
    when:
      sut.serialize(impossibleType, APPLICATION_JSON)
    then:
      thrown(IllegalArgumentException)
  }

  def "should find compatible types"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
      def jsonMapper = Mock(ObjectMapper)
      sut.mappers.put(APPLICATION_JSON, jsonMapper)
    expect:
    APPLICATION_JSON == sut.findAvailableMediaType(parseMediaTypes("*/*"))
  }

  def "should return null on incompatible type"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
    expect:
    null == sut.findAvailableMediaType([APPLICATION_JSON])
  }

  def "should return null on no compatible type"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
      def jsonMapper = Mock(ObjectMapper)
      sut.mappers.put(APPLICATION_JSON, jsonMapper)
    expect:
    null == sut.findAvailableMediaType([APPLICATION_YAML])
  }

  def "should default to json"() {
    given:
      MultiFormatSerializer sut = new MultiFormatSerializer([], [])
    expect:
    APPLICATION_JSON == sut.findAvailableMediaType([])
  }

  def "should serialize with custom registrars"() {
    given: "mocks"
      def registrar = Mock(JacksonModuleRegistrar)
      def jsonMapper = Mock(ObjectMapper)
      def yamlMapper = Mock(ObjectMapper)

      def jsonMapperConfigurer = Mock(CustomFormatOutputMapper) {
        configureMapper() >> jsonMapper
        getFormats() >> [APPLICATION_JSON]
      }

    def yamlMapperConfigurer = Mock(CustomFormatOutputMapper) {
        configureMapper() >> yamlMapper
        getFormats() >> [APPLICATION_YAML]
      }

      def object = 'a string'
    when:
      MultiFormatSerializer sut = new MultiFormatSerializer([registrar], [jsonMapperConfigurer, yamlMapperConfigurer])
    and:
      sut.serialize(object, APPLICATION_YAML)
    then:
      1 * yamlMapper.writeValueAsString(object)
      0 * jsonMapper.writeValueAsString(object)
      2 * registrar.maybeRegisterModule(_)
  }
}
