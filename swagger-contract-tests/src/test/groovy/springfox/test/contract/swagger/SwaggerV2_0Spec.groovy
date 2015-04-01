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
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.AuthorizationType
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static groovyx.net.http.ContentType.*
import static springfox.documentation.builders.PathSelectors.*

@ContextConfiguration(loader = SpringApplicationContextLoader,
        classes = SwaggerV2_0Spec.Config)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
class SwaggerV2_0Spec extends Specification implements springfox.test.contract.swagger.FileAccess {

  @Value('${local.server.port}')
  int port;

  @Unroll("#groupName")
  def 'should honor swagger resource listing'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents("/contract/swagger2/$contractFile")

    when:
      def response = http.get(
              path: '/v2/api-docs',
              query: [group: groupName],
              contentType: TEXT, //Allows to access the raw response body
              headers: [Accept: 'application/json']
      )
    then:
      String raw = response.data.text
      String actual = JsonOutput.prettyPrint(raw)
      response.status == 200
//      println(actual)

    def withPortReplaced = contract.replaceAll("__PORT__", "$port")
    JSONAssert.assertEquals(withPortReplaced, actual, JSONCompareMode.NON_EXTENSIBLE)

    where:
      contractFile                                                  | groupName
      'swagger.json'                                                | 'petstore'
      'declaration-business-service.json'                           | 'businessService'
      'declaration-concrete-controller.json'                        | 'concrete'
      'declaration-controller-with-no-request-mapping-service.json' | 'noRequestMapping'
      'declaration-fancy-pet-service.json'                          | 'fancyPetstore'
      'declaration-feature-demonstration-service.json'              | 'featureService'
      'declaration-feature-demonstration-service-codeGen.json'      | 'featureService-codeGen'
      'declaration-inherited-service-impl.json'                     | 'inheritedService'
      'declaration-pet-grooming-service.json'                       | 'petGroomingService'
      'declaration-pet-service.json'                                | 'petService'
//      'declaration-root-controller.json'                            | 'root'
  }

  @Configuration
  @EnableSwagger2
  @ComponentScan([
          "springfox.documentation.spring.web.dummy.controllers",
          "springfox.test.contract.swagger",
          "springfox.petstore.controller"
  ])
  @Import(AuthorizationSupport)
  static class Config {
    @Bean
    public Docket petstore(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("petstore")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/api/.*"))
                .build()
    }

    @Bean
    public Docket business(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("businessService")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/business.*"))
                .build()
    }

    @Bean
    public Docket concrete(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("concrete")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/foo/.*"))
                .build()
    }

    @Bean
    public Docket noRequestMapping(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("noRequestMapping")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/no-request-mapping/.*"))
                .build()
    }

    @Bean
    public Docket fancyPetstore(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("fancyPetstore")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/fancypets/.*"))
                .build()
    }

    @Bean
    public Docket featureService(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("featureService")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/features/.*"))
                .build()
    }

    @Bean
    public Docket inheritedService(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("inheritedService")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/child/.*"))
                .build()
    }

    @Bean
    public Docket pet(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("petService")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/pets/.*"))
                .build()
    }

    @Bean
    public Docket petGrooming(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("petGroomingService")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/petgrooming/.*"))
                .build()
    }

    @Bean
    public Docket root(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("root")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
                .paths(regex("/.*"))
                .build()
    }

    @Bean
    public Docket featureServiceForCodeGen(List<AuthorizationType> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
              .groupName("featureService-codeGen")
              .useDefaultResponseMessages(false)
              .authorizationTypes(authorizationTypes)
              .forCodeGeneration(true)
              .produces(['application/xml', 'application/json'] as Set)
              .select()
              .paths(regex("/features/.*"))
              .build()
    }
  }
}
