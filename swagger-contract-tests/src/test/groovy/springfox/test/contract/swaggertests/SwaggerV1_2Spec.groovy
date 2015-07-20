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

package springfox.test.contract.swaggertests

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger1.annotations.EnableSwagger

import static com.google.common.collect.Lists.*
import static groovyx.net.http.ContentType.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*
import static springfox.documentation.builders.PathSelectors.*

@ContextConfiguration(
    loader = SpringApplicationContextLoader,
    classes = Config)
class SwaggerV1_2Spec extends SwaggerAppSpec implements FileAccess {

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
    expect:
      testCases().each {
        String contract = fileContents("/contract/swagger/$it.contract")
        def response = http.get(
            path: "/api-docs$it.declarationPath",
            contentType: TEXT, //Allow access to the raw response body
            headers: [Accept: 'application/json']
        )
        response.status == 200
        //Uncomment this to see a better json diff when tests fail
        //println(actual)
        JSONAssert.assertEquals(contract, JsonOutput.prettyPrint(response.data.text), NON_EXTENSIBLE)
        true
      }
  }

  def testCases() {
    [
        [contract: 'declaration-business-service.json',
        declarationPath: '/default/business-service'],
        [contract: 'declaration-concrete-controller.json',
         declarationPath: '/default/concrete-controller'],
        [contract: 'declaration-controller-with-no-request-mapping-service.json',
         declarationPath:  '/default/controller-with-no-request-mapping-service'],
        [contract: 'declaration-fancy-pet-service.json',
         declarationPath:  '/default/fancy-pet-service'],
        [contract: 'declaration-feature-demonstration-service.json',
         declarationPath: '/default/feature-demonstration-service'],
        [contract: 'declaration-inherited-service-impl.json',
         declarationPath:  '/default/inherited-service-impl'],
        [contract: 'declaration-pet-grooming-service.json',
         declarationPath:  '/default/pet-grooming-service'],
        [contract: 'declaration-pet-service.json',
         declarationPath:  '/default/pet-service'],
        [contract: 'declaration-root-controller.json',
         declarationPath:  '/default/root-controller'],
        [contract: 'declaration-groovy-service.json',
         declarationPath: '/default/groovy-service']]
  }

  def "should list swagger resources"() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
    when:
      def response = http.get(path: '/swagger-resources', contentType: TEXT, headers: [Accept: 'application/json'])
      def slurper = new JsonSlurper()
      def result = slurper.parseText(response.data.text)
      println "Results: "
      result.each {
        println it
      }
    then:
      result.find { it.name == 'default' && it.location == '/api-docs' && it.swaggerVersion == '1.2' }
  }

  @Configuration
  @EnableSwagger
  @ComponentScan([
      "springfox.documentation.spring.web.dummy.controllers",
      "springfox.test.contract.swagger",
      "springfox.petstore.controller"
  ])
  @Import(SecuritySupport)
  static class Config {

    @Bean
    SecurityContext securityContext() {
      def readScope = new AuthorizationScope("read:pets", "read your pets")
      def scopes = new AuthorizationScope[1]
      scopes[0] = readScope
      SecurityReference securityReference = SecurityReference.builder()
          .reference("petstore_auth")
          .scopes(scopes)
          .build()

      SecurityContext.builder()
          .securityReferences(newArrayList(securityReference))
          .forPaths(ant("/petgrooming/**"))
          .build()
    }

    @Bean
    public Docket testCases(List<SecurityScheme> securitySchemes, List<SecurityContext> securityContexts) {
      return new Docket(DocumentationType.SWAGGER_12)
          .groupName("default")
          .select()
          .paths(regex("^((?!/api).)*\$")) //Not beginning with /api
          .build()
          .securitySchemes(securitySchemes)
          .securityContexts(securityContexts)
          .ignoredParameterTypes(MetaClass)
    }


  }
}
