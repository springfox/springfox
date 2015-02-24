package com.mangofactory.test.contract.swagger

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static groovyx.net.http.ContentType.*

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Swagger2Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
class SwaggerV2_0Spec extends Specification implements FileAccess {

  @Value('${local.server.port}')
  int port;

  def 'should honor swagger resource listing'() {
    given:
      RESTClient http = new RESTClient("http://localhost:$port")
//      String contract = fileContents('resource-listing.json')

    when:
      def response = http.get(
              path: '/api-docs',
//              query: [group: 'petstore'],
              contentType: TEXT, //Allows to access the raw response body
              headers: [Accept: 'application/json']
      )
    then:
      String raw = response.data.text
      String actual = JsonOutput.prettyPrint(raw)
      response.status == 200
      println(actual)

//      JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)
  }


}
