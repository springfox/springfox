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

package springfox.test.contract.swagger
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.annotations.EnableSwagger

import static groovyx.net.http.ContentType.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static springfox.documentation.builders.PathSelectors.*

@ContextConfiguration(
        loader = SpringApplicationContextLoader,
        classes = SwaggerV1_2Spec.Config)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
class SwaggerV1_2Spec extends Specification implements FileAccess {

  @Value('${local.server.port:8080}')
  int port;

  def 'should honor swagger resource listing'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents('/contract/swagger/resource-listing.json')

    when:
      def response = http.get(
              path: '/api-docs',
              contentType: TEXT, //Allows to access the raw response body
              headers: [Accept: 'application/json']
      )
    then:
      String raw = response.data.text
      String actual = JsonOutput.prettyPrint(raw)
      response.status == 200

      JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)
  }

  @Unroll
  def 'should honor api declaration contract [#contractFile] at endpoint [#declarationPath]'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents("/contract/swagger/$contractFile")
    when:
      def response = http.get(
              path: "/api-docs${declarationPath}",
              contentType: TEXT, //Allow access to the raw response body
              headers: [Accept: 'application/json']
      )
    then:
      String raw = response.data.text
      String actual = JsonOutput.prettyPrint(raw)
      response.status == 200
      //Uncomment this to see a better json diff when tests fail
//      actual == contract
//      println(actual)

      JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)

//    and: "both json docs are the same length"
//      contract.length() == actual.length()

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
  }
  
  @Configuration
  @EnableSwagger
  @ComponentScan([
    "springfox.documentation.spring.web.dummy.controllers",
    "springfox.test.contract.swagger",
    "springfox.petstore.controller"
  ])
  static class Config {
    @Bean
    public Docket testCases() {
      return new Docket(DocumentationType.SWAGGER_12)
              .groupName("default")
              .select()
                .paths(regex("^((?!/api).)*\$")) //Not beginning with /api
                .build()
    }
  }
}
