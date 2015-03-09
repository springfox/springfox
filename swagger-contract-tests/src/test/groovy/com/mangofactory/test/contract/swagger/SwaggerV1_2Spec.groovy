package com.mangofactory.test.contract.swagger
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.skyscreamer.jsonassert.JSONCompareMode.*

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
class SwaggerV1_2Spec extends Specification {

  public static final boolean STRICT_JSON_ASSERT = false
  @Value('${local.server.port}')
  int port;

  def 'should honor swagger resource listing'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents('resource-listing.json')

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
//      println(actual)

      JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)
  }

  @Unroll
  def 'should honor api declaration contract [#contractFile] at endpoint [#declarationPath]'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
      String contract = fileContents(contractFile)
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
      println(actual)

      //Json comparison without considering ordering
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

  private String fileContents(String fileName) {
    this.getClass().getResource("/contract/swagger/$fileName").text
  }
}
