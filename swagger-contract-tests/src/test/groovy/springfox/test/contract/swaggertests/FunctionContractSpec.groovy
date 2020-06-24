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

package springfox.test.contract.swaggertests

import com.fasterxml.classmate.TypeResolver
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
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
import springfox.test.contract.swagger.SwaggerApplication

import static java.nio.charset.StandardCharsets.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static org.springframework.boot.test.context.SpringBootTest.*

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = SwaggerApplication,
    properties = ["logging.level.springfox.documentation=DEBUG"]
)
class FunctionContractSpec extends Specification implements FileAccess {

  @Shared
  def http = new TestRestTemplate(new RestTemplateBuilder().additionalMessageConverters(new StringHttpMessageConverter(UTF_8)))

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
    contractFile                                                  | groupName
    'swagger.json'                                                | 'petstore'
    'swaggerTemplated.json'                                       | 'petstoreTemplated'
    'declaration-bugs-service.json'                               | 'bugs'
    'declaration-bugs-different-service.json'                     | 'bugsDifferent'
    'declaration-business-service.json'                           | 'businessService'
    'declaration-concrete-controller.json'                        | 'concrete'
    'declaration-controller-with-no-request-mapping-service.json' | 'noRequestMapping'
    'declaration-fancy-pet-service.json'                          | 'fancyPetstore'
    'declaration-feature-demonstration-service.json'              | 'featureService'
    'declaration-feature-demonstration-service-codeGen.json'      | 'featureService-codeGen'
    'declaration-inherited-service-impl.json'                     | 'inheritedService'
    'declaration-pet-grooming-service.json'                       | 'petGroomingService'
    'declaration-pet-service.json'                                | 'petService'
    'declaration-groovy-service.json'                             | 'groovyService'
    'declaration-enum-service.json'                               | 'enumService'
    'declaration-spring-data-rest.json'                           | 'spring-data-rest'
    'declaration-consumes-produces-not-on-document-context.json'  | 'consumesProducesNotOnDocumentContext'
    'declaration-consumes-produces-on-document-context.json'      | 'consumesProducesOnDocumentContext'
    'declaration-same-controller.json'                            | 'same'
    'declaration-cyclic-controller.json'                          | 'cyclic'
  }

  def "should list swagger resources for swagger 2.0"() {
    given:
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
      it.name == 'businessService' &&
          it.url == '/v2/api-docs?group=businessService' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'concrete' &&
          it.url == '/v2/api-docs?group=concrete' &&
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
    def response = http.exchange(request, String)

    then:
    response.statusCode == HttpStatus.OK

    def bodyWithLFOnly = response.body.replaceAll("\\\\r\\\\n", "\\\\n") //Make sure if we're running on windows the line endings which are double-escaped match up with the resource file above.
    JSONAssert.assertEquals(contract, bodyWithLFOnly, NON_EXTENSIBLE)
  }

  @Ignore
  @Unroll
  def 'should honor api v1.2 contract [#contractFile] at endpoint [#declarationPath]'() {
    given:
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/api-docs${declarationPath}"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents("/contract/swagger/$contractFile")

    when:
    def response = http.exchange(request, String)

    then:
    String raw = response.body
    response.statusCode == HttpStatus.OK
    maybeWriteToFile(
        "/contract/swagger/$contractFile",
        raw.replace("localhost:$port", "localhost:__PORT__"))
    JSONAssert.assertEquals(contract, raw, NON_EXTENSIBLE)

    where:
    contractFile                                                  | declarationPath
    'declaration-business-service.json'                           | '/default/business-service'
    'declaration-concrete-controller.json'                        | '/default/concrete-controller'
    'declaration-controller-with-no-request-mapping-service.json' | '/default/controller-with-no-request-mapping-service'
    'declaration-fancy-pet-service.json'                          | '/default/fancy-pet-service'
    'declaration-feature-demonstration-service.json'              | '/default/feature-demonstration-service'
    'declaration-inherited-service-impl.json'                     | '/default/inherited-service-impl'
    'declaration-pet-grooming-service.json'                       | '/default/pet-grooming-service'
    'declaration-pet-service.json'                                | '/default/pet-service'
    'declaration-root-controller.json'                            | '/default/root-controller'
    'declaration-groovy-service.json'                             | '/default/groovy-service'
  }


  @Ignore
  def "should list swagger resources for swagger 1.2"() {
    given:
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/swagger-resources"))
        .accept(MediaType.APPLICATION_JSON)
        .build()

    when:
    def response = http.exchange(request, String)
    def slurper = new JsonSlurper()
    def result = slurper.parseText(response.body)

    then:
    result.find {
      it.name == 'default' &&
          it.url == '/api-docs' &&
          it.swaggerVersion == '1.2'
    }
  }

  @TestConfiguration
  static class Config {

// tag::alternate-type-rule-convention[]
    @Bean
    AlternateTypeRuleConvention jacksonSerializerConvention(TypeResolver resolver) {
      new JacksonSerializerConvention(resolver, "springfox.documentation.spring.web.dummy.models")
    }
// end::alternate-type-rule-convention[]
  }
}