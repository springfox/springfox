/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package springfox.test.contract.swagger

import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static groovyx.net.http.ContentType.*

@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
abstract class SwaggerAppSpec extends Specification {

  @Value('${local.server.port}')
  int port;

  def "should list swagger resources"() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
    when:
      def response = http.get(path: '/swagger-resources', contentType: TEXT, headers: [Accept: 'application/json'])
      def slurper = new JsonSlurper()
      def result = slurper.parseText(response.data.text)
    then:
      result.find { it.name == 'default' && it.location == '/api-docs?group=default' && it.swaggerVersion == '1.2' }
      result.find { it.name == 'default' && it.location == '/v2/api-docs?group=default' && it.swaggerVersion == '2.0' }
  }
}
