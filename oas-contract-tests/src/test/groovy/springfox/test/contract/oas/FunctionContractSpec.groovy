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
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.AlternateTypeRuleConvention
import springfox.documentation.spring.web.plugins.JacksonSerializerConvention

import static java.nio.charset.StandardCharsets.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static org.springframework.boot.test.context.SpringBootTest.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = OasApplication)
@Ignore
class FunctionContractSpec extends Specification implements FileAccess {

  @Shared
  def http = new TestRestTemplate(
      new RestTemplateBuilder().additionalMessageConverters(
          new StringHttpMessageConverter(
              UTF_8)))

  @LocalServerPort
  int port

  @Unroll
  def 'should honor open api 3.0 resource listing #groupName'() {
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
//    'petstoreTemplated.json'                                       | 'petstoreTemplated'
//    'declaration-business-service.json'                           | 'businessService'
//    'declaration-concrete-controller.json'                        | 'concrete'
//    'declaration-controller-with-no-request-mapping-service.json' | 'noRequestMapping'
//    'declaration-fancy-pet-service.json'                          | 'fancyPetstore'
//    'declaration-inherited-service-impl.json'                     | 'inheritedService'
//    'declaration-pet-grooming-service.json'                       | 'petGroomingService'
//    'declaration-pet-service.json'                                | 'petService'
//    'declaration-groovy-service.json'                             | 'groovyService'
//    'declaration-enum-service.json'                               | 'enumService'
//    'declaration-spring-data-rest.json'                           | 'spring-data-rest'
//    'declaration-consumes-produces-not-on-document-context.json'  | 'consumesProducesNotOnDocumentContext'
//    'declaration-consumes-produces-on-document-context.json'      | 'consumesProducesOnDocumentContext'
//    'declaration-same-controller.json'                            | 'same'
  }

  def "should list swagger resources for open api 3.0"() {
    given:
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/oas-resources"))
        .accept(MediaType.APPLICATION_JSON)
        .build()

    when:
    def response = http.
        exchange(
            request,
            String)
    def slurper = new JsonSlurper()
    def result = slurper.parseText(response.body)

    then:
    result.find {
      it.name == 'petstore' &&
          it.url == '/v3/api-docs?group=petstore' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'businessService' &&
          it.url == '/v3/api-docs?group=businessService' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'concrete' &&
          it.url == '/v3/api-docs?group=concrete' &&
          it.swaggerVersion == '2.0'
    }
  }

  @Ignore
  def 'should honor swagger resource listing'() {
    given:
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/api-docs"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents('/contract/swagger/resource-listing.json')

    when:
    def response = http.
        exchange(
            request,
            String)

    then:
    response.statusCode == HttpStatus.OK

    def bodyWithLFOnly = response.
        body.
        replaceAll(
            "\\\\r\\\\n",
            "\\\\n") //Make sure if we're running on windows the line endings which are double-escaped match up with
    // the resource file above.
    JSONAssert.
        assertEquals(
            contract,
            bodyWithLFOnly,
            NON_EXTENSIBLE)
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
    // tag::alternate-type-rule-convention[]
  }
}