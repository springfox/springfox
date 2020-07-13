/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

package springfox.test.contract.swaggertests.webflux


import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.test.contract.swagger.webflux.SwaggerWebfluxApplication

import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static org.springframework.boot.test.context.SpringBootTest.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SwaggerWebfluxApplication)
class WebFluxFunctionContractSpec extends Specification implements FileAccess {

  @Shared
  def http = new TestRestTemplate()

  @Value('${local.server.port}')
  int port

  @Unroll
  def 'should honor swagger v2 resource listing #groupName'() {
    given:
    RequestEntity<Void> request = RequestEntity.get(
        new URI("http://localhost:$port/v2/api-docs?group=$groupName"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents("/contract/swagger2/$contractFile")

    when:
    def response = http.exchange(request, String)
    then:
    String raw = response.body
    response.statusCode == HttpStatus.OK

    def withPortReplaced = contract.replaceAll("__PORT__", "$port")
    maybeWriteToFile(
        "/contract/swagger2/$contractFile",
        raw.replace("localhost:$port", "localhost:__PORT__"))
    JSONAssert.assertEquals(withPortReplaced, raw, NON_EXTENSIBLE)

    where:
    contractFile                 | groupName
    'swagger.json'               | 'petstore'
    'swaggerTemplated.json'      | 'petstoreTemplated'
    'feature-demonstration.json' | 'features'
    'bug-demonstration.json'     | 'bugs'

  }

  def "should list swagger resources for swagger 2.0"() {
    given:
    def http = new TestRestTemplate()
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/swagger-resources"))
        .accept(MediaType.APPLICATION_JSON)
        .build()

    when:
    def response = http.exchange(request, String)
    def slurper = new JsonSlurper()
    def result = slurper.parseText(response.body)

    then:
    result.find {
      it.name == 'petstore' &&
          it.url == '/v2/api-docs?group=petstore' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'petstoreTemplated' &&
          it.url == '/v2/api-docs?group=petstoreTemplated' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'bugs' &&
          it.url == '/v2/api-docs?group=bugs' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'features' &&
          it.url == '/v2/api-docs?group=features' &&
          it.swaggerVersion == '2.0'
    }
  }
}