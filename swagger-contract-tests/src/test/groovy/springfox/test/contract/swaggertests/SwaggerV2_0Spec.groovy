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
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll
import springfox.documentation.OperationNameGenerator
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static groovyx.net.http.ContentType.*
import static springfox.documentation.builders.PathSelectors.*

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Config)
class SwaggerV2_0Spec extends SwaggerAppSpec implements FileAccess {

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
      println(actual)

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
      'declaration-groovy-service.json'                             | 'groovyService'
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
      result.find { it.name == 'petstore' && it.location == '/v2/api-docs?group=petstore' && it.swaggerVersion == '2.0' }
      result.find {
        it.name == 'businessService' && it.location == '/v2/api-docs?group=businessService' && it.swaggerVersion == '2.0'
      }
      result.find { it.name == 'concrete' && it.location == '/v2/api-docs?group=concrete' && it.swaggerVersion == '2.0' }
  }

  @Configuration
  @EnableCaching
  @EnableSwagger2
  @ComponentScan([
      "springfox.documentation.spring.web.dummy.controllers",
      "springfox.test.contract.swagger",
      "springfox.petstore.controller"
  ])
  @Import(SecuritySupport)
  static class Config {

    //This is so as to predictably generate the same names
    @Bean
    @Primary
    OperationNameGenerator nameGenerator() {
      new OperationNameGenerator() {
        @Override
        String startingWith(String prefix) {
          return prefix;
        }
      }
    }

    @Bean
    @Autowired
    public CacheManager cacheManager(List<Cache> caches) {
      def cacheManager = new SimpleCacheManager()
      cacheManager.caches = caches
      return cacheManager;
    }
    @Bean
    public Docket petstore(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("petstore")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/api/.*"))
          .build()
    }

    @Bean
    public Docket business(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("businessService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/business.*"))
          .build()
    }

    @Bean
    public Docket concrete(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("concrete")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/foo/.*"))
          .build()
    }

    @Bean
    public Docket noRequestMapping(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("noRequestMapping")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/no-request-mapping/.*"))
          .build()
    }

    @Bean
    public Docket fancyPetstore(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("fancyPetstore")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/fancypets/.*"))
          .build()
    }

    @Bean
    public Docket featureService(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("featureService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/features/.*"))
          .build()
    }

    @Bean
    public Docket inheritedService(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("inheritedService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/child/.*"))
          .build()
    }

    @Bean
    public Docket pet(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("petService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/pets/.*"))
          .build()
    }

    @Bean
    public Docket petGrooming(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("petGroomingService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/petgrooming/.*"))
          .build()
    }

    @Bean
    public Docket root(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("root")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .produces(['application/xml', 'application/json'] as Set)
          .ignoredParameterTypes(MetaClass)
          .select()
            .paths(regex("/.*"))
          .build()
    }

    @Bean
    public Docket groovyServiceBean(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("groovyService")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .forCodeGeneration(true)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/groovy/.*"))
          .build()
          .ignoredParameterTypes(MetaClass)
    }

    @Bean
    public Docket featureServiceForCodeGen(List<SecurityScheme> authorizationTypes) {
      return new Docket(DocumentationType.SWAGGER_2)
          .groupName("featureService-codeGen")
          .useDefaultResponseMessages(false)
          .securitySchemes(authorizationTypes)
          .forCodeGeneration(true)
          .produces(['application/xml', 'application/json'] as Set)
          .select()
          .paths(regex("/features/.*"))
          .build()
    }
  }
}
