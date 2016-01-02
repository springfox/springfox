package springfox.test.contract.swaggertests
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.skyscreamer.jsonassert.JSONCompareMode.*

@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestExecutionListeners([DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener])
@ContextConfiguration(
    loader = SpringApplicationContextLoader,
    classes = Config)
public class FunctionContractSpec extends Specification implements FileAccess {

  @Shared
  def http = new TestRestTemplate()

  @Value('${local.server.port}')
  int port;

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
    String actual = JsonOutput.prettyPrint(raw)
    response.statusCode == HttpStatus.OK
//      println(actual)

    def withPortReplaced = contract.replaceAll("__PORT__", "$port")
    JSONAssert.assertEquals(withPortReplaced, actual, JSONCompareMode.NON_EXTENSIBLE)

    where:
    contractFile                                                  | groupName
    'swagger.json'                                                | 'petstore'
    'swaggerTemplated.json'                                       | 'petstoreTemplated'
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
  }

  def "should list swagger resources for swagger 2.0"() {
    given:
    def http = new TestRestTemplate()
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
          it.location == '/v2/api-docs?group=petstore' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'businessService' &&
          it.location == '/v2/api-docs?group=businessService' &&
          it.swaggerVersion == '2.0'
    }
    result.find {
      it.name == 'concrete' &&
          it.location == '/v2/api-docs?group=concrete' &&
          it.swaggerVersion == '2.0'
    }
  }

  def 'should honor swagger resource listing'() {
    given:
    def http = new TestRestTemplate()
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/api-docs"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents('/contract/swagger/resource-listing.json')

    when:
    def response = http.exchange(request, String)
    then:
    String actual = JsonOutput.prettyPrint(response.body)
    response.statusCode == HttpStatus.OK
//      println(actual)

    JSONAssert.assertEquals(contract, actual, NON_EXTENSIBLE)
  }

  @Unroll
  def 'should honor api v1.2 contract [#contractFile] at endpoint [#declarationPath]'() {
    given:
    def http = new TestRestTemplate()
    RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/api-docs${declarationPath}"))
        .accept(MediaType.APPLICATION_JSON)
        .build()
    String contract = fileContents("/contract/swagger/$contractFile")
    when:
    def response = http.exchange(request, String)
    then:
    String raw = response.body
    String actual = JsonOutput.prettyPrint(raw)
    response.statusCode == HttpStatus.OK
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
    'declaration-groovy-service.json'                             | '/default/groovy-service'
  }


  def "should list swagger resources for swagger 1.2"() {
    given:
    def http = new TestRestTemplate()
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
          it.location == '/api-docs' &&
          it.swaggerVersion == '1.2'
    }
  }

  @Configuration
  @ComponentScan([
    "springfox.documentation.spring.web.dummy.controllers",
    "springfox.test.contract.swagger",
    "springfox.petstore.controller"
  ])
  @Import([SecuritySupport, Swagger12TestConfig, Swagger2TestConfig])
  static class Config {
  }
}