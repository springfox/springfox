package com.mangofactory.test.contract.swagger

import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spring.web.plugins.DocumentationConfigurer
import com.mangofactory.documentation.swagger.annotations.EnableSwagger
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

import static groovyx.net.http.ContentType.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*

@ContextConfiguration(
        loader = SpringApplicationContextLoader,
        classes = SwaggerV1_2Spec.Config)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
class SwaggerV1_2Spec extends Specification implements FileAccess {

  @Value('${local.server.port}')
  int port;

  def 'should honor swagger resource listing'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents('/contract/swagger/resource-listing.json')

    when:
      def response = http.get(
              path: '/v1/api-docs',
              contentType: TEXT, //Allows to access the raw response body
              headers: [Accept: 'application/json']
      )
    then:
      String raw = response.data.text
      String actual = JsonOutput.prettyPrint(raw)
      response.status == 200
//      println(actual)

      JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)
  }

  @Unroll
  def 'should honor api declaration contract [#contractFile] at endpoint [#declarationPath]'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents("/contract/swagger/$contractFile")
    when:
      def response = http.get(
              path: "/v1/api-docs${declarationPath}",
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
    "com.mangofactory.documentation.spring.web.dummy.controllers",
    "com.mangofactory.test.contract.swagger",
    "com.mangofactory.petstore.controller"
  ])
  static class Config {
    @Bean
    public DocumentationConfigurer testCases() {
      return new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .groupName("default")
              .includePatterns("^((?!/api).)*\$"); //Not beginning with /api
    }
  }
}
