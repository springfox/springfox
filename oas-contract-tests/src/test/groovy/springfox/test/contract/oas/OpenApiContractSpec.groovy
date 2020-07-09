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

package springfox.test.contract.oas

import com.fasterxml.classmate.TypeResolver
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.converter.StringHttpMessageConverter
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.AlternateTypeRuleConvention
import springfox.documentation.spring.web.plugins.JacksonSerializerConvention

import static java.nio.charset.StandardCharsets.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static org.springframework.boot.test.context.SpringBootTest.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OpenApiApplication)
class OpenApiContractSpec extends Specification implements FileAccess {

  @Shared
  def http = new TestRestTemplate(
      new RestTemplateBuilder().additionalMessageConverters(
          new StringHttpMessageConverter(
              UTF_8)))

  @LocalServerPort
  int port

  @Value('${springfox.documentation.swagger-ui.base-url}')
  String baseUrl;

  def 'should honor open api 3.0 resource listing'() {
    given:
    RequestEntity<Void> request = RequestEntity.get(
        new URI("http://localhost:$port/v3/api-docs?group=$groupName"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents("/contracts/$contractFile")

    when:
    def response = http.
        exchange(
            request,
            String)
    then:
    String raw = response.body
    response.statusCode == HttpStatus.OK

    def withPortReplaced = contract.
        replaceAll(
            "__PORT__",
            "$port")
    maybeWriteToFile(
        "/contracts/$contractFile",
        raw.
            replace(
                "localhost:$port",
                "localhost:__PORT__"))
    JSONAssert.
        assertEquals(
            withPortReplaced,
            raw,
            NON_EXTENSIBLE)

    where:
    contractFile    | groupName
    'petstore.json' | 'petstore'
    'bugs.json'     | 'bugs'
    'features.json' | 'features'
  }

  def "should list swagger resources for open api 3.0"() {
    given:
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port$baseUrl/swagger-resources"))
        .accept(MediaType.APPLICATION_JSON)
        .build()

    when:
    def response = http.exchange(
        request,
        String)
    def slurper = new JsonSlurper()
    def result = slurper.parseText(response.body)

    then:
    result.find {
      it.name == 'bugs' &&
          it.url == "/v3/api-docs?group=bugs" &&
          it.swaggerVersion == '3.0.3'
    }
    result.find {
      it.name == 'features' &&
          it.url == "/v3/api-docs?group=features" &&
          it.swaggerVersion == '3.0.3'
    }
    result.find {
      it.name == 'petstore' &&
          it.url == "/v3/api-docs?group=petstore" &&
          it.swaggerVersion == '3.0.3'
    }
    result.find {
      it.name == 'default' &&
          it.url == "/v3/api-docs" &&
          it.swaggerVersion == '3.0.3'
    }
  }

  @TestConfiguration
  static class Config {

    // tag::alternate-type-rule-convention[]
    @Bean
    AlternateTypeRuleConvention jacksonSerializerConvention(TypeResolver resolver) {
      new JacksonSerializerConvention(
          resolver,
          "springfox.documentation.spring.web.dummy.models")
    }
    // end::alternate-type-rule-convention[]
  }
}