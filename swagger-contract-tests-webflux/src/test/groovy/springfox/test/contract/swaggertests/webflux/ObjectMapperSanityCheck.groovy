/*
 *
 *
 *
 *
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package springfox.test.contract.swaggertests.webflux

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.test.contract.swagger.webflux.SwaggerWebfluxApplication
import springfox.test.contract.swagger.webflux.listeners.ObjectMapperEventListener

import static org.springframework.boot.test.context.SpringBootTest.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Config)
class ObjectMapperSanityCheck extends Specification {

  @Value('${local.server.port}')
  int port

  def "should produce valid swagger json regardless of object mapper configuration"() {

    given: "A customized object mapper always serializing empty attributes"
      def http = new TestRestTemplate()
      RequestEntity<Void> request = RequestEntity.get(new URI("http://localhost:$port/v2/api-docs?group=default"))
        .accept(MediaType.APPLICATION_JSON)
        .build()

    when: "swagger json is produced"
      def response = http.exchange(request, String)

    then: "There should not be a null schemes element"
      def slurper = new JsonSlurper()
      def swagger = slurper.parseText(response.body)
      !swagger.containsKey('schemes')
  }

  @Configuration
  @EnableSwagger2
  @ComponentScan(basePackageClasses = [SwaggerWebfluxApplication.class])
  static class Config {
    @Bean
    public Docket testCases() {
      return new Docket(DocumentationType.SWAGGER_2).select().build()
    }

    @Bean
    @Primary
    public ObjectMapper objectMapperWithIncludeAlways(){
      /* Replaces Spring Boot's object mapper
       * http://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html
       */
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true )
      objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
      return objectMapper
    }

    @Bean
    public ObjectMapperEventListener objectMapperEventListener(){
      //Register an ObjectMapperConfigured event listener
      return new ObjectMapperEventListener()
    }


    @Bean
    static PropertySourcesPlaceholderConfigurer properties() throws Exception {
      return new PropertySourcesPlaceholderConfigurer()
    }
  }
}
