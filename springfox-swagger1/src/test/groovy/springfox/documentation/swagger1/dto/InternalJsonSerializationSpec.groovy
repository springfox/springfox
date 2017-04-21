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

package springfox.documentation.swagger1.dto

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.swagger1.configuration.SwaggerJacksonModule

class InternalJsonSerializationSpec extends Specification {

  @Shared ObjectMapper objectMapper

  def setupSpec() {
    def module = new SwaggerJacksonModule()
    objectMapper = new ObjectMapper()
    objectMapper.registerModule(module)
  }

  def writeAndParse(object, boolean print = true) {

    def jsonString = objectMapper.writeValueAsString(object)
    def json = new JsonSlurper().parseText(jsonString)
    if (print) {
      println jsonString
    }
    json
  }

  def writePretty(object){
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object).normalize()
  }
}