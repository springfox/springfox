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

package springfox.documentation.swagger1.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springfox.documentation.swagger1.configuration.SwaggerJacksonModule
import springfox.documentation.swagger1.dto.ApiListing

class SwaggerJacksonModuleSpec extends Specification {

  def "should create serialization module"() {
    ObjectMapper objectMapper = new ObjectMapper()
    new SwaggerJacksonModule().maybeRegisterModule(objectMapper)

    expect:
    objectMapper.findMixInClassFor(ApiListing) != null
  }

  def "should create serialization module only once"() {
    ObjectMapper objectMapper = new ObjectMapper()
    new SwaggerJacksonModule().maybeRegisterModule(objectMapper)
    new SwaggerJacksonModule().maybeRegisterModule(objectMapper)

    expect:
    objectMapper.findMixInClassFor(ApiListing) != null
  }
}
