/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.service.model.builder

import org.springframework.http.HttpMethod
import spock.lang.Specification
import springfox.documentation.OperationNameGenerator
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.Example
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ResponseMessage
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.VendorExtension

import java.util.stream.Collectors

import static java.util.Collections.*

class OperationBuilderSpec extends Specification {
  def nameGenerator = Mock(OperationNameGenerator)
  OperationBuilder sut = new OperationBuilder(nameGenerator)
  ResponseMessage partialOk = new ResponseMessageBuilder()
      .code(200)
      .message(null)
      .responseModel(null)
      .build()
  ResponseMessage fullOk = new ResponseMessageBuilder()
      .code(200)
      .message("OK")
      .responseModel(new ModelRef("String"))
      .examples([new Example("application/json", "{\"foo\":  42}")])
      .build()

  def "Parameters are ordered by order first, then alphabetically" () {
    when:
      sut.parameters(Arrays.asList(
        new ParameterBuilder().name("a").order(2).build(),
        new ParameterBuilder().name("b").order(2).build(),
        new ParameterBuilder().name("z").order(1).build(),
        new ParameterBuilder().name("y").order(1).build()
      ))
    and:
      def operation = sut.build()
    then:
      operation.parameters.stream().map { it.name }.collect(Collectors.toList()) == Arrays.asList("y", "z", "a", "b")
  }

  def "Merges response messages when new response messages are applied" () {
    given:
    sut.responseMessages(singleton(partialOk))
    when:
    nameGenerator.startingWith(_) >> _
    sut.responseMessages(singleton(fullOk))
    and:
    def operation = sut.build()
    then:
    operation.responseMessages.size() == 1
    operation.responseMessages.first().code == 200
    operation.responseMessages.first().message == "OK"
    operation.responseMessages.first().responseModel.type == "String"
    operation.responseMessages.first().responseModel.itemType == null
  }

  def "Response message builder is non-destructive"() {
    given:
    sut.responseMessages(singleton(fullOk))
    when:
    sut.responseMessages(singleton(partialOk))
    and:
    def operation = sut.build()
    then:
    operation.responseMessages.size() == 1
    operation.responseMessages.first().code == 200
    operation.responseMessages.first().message == "OK"
    operation.responseMessages.first().responseModel.type == "String"
    operation.responseMessages.first().responseModel.itemType == null
    operation.responseMessages.first().examples.size() == 1
  }

  def "Setting properties on the builder with non-null values"() {
    given:
    def sut = new OperationBuilder(nameGenerator)
    when:
    nameGenerator.startingWith(_) >> "method1"
    sut."$builderMethod"(value)
    and:
    def built = sut.build()
    then:
    built."$property" == value

    where:
    builderMethod   | value                                      | property
    'method'        | HttpMethod.GET                             | 'method'
    'summary'       | 'method1 summary'                          | 'summary'
    'notes'         | 'method1 notes'                            | 'notes'
    'responseModel' | new ModelRef('string')                     | 'responseModel'
    'deprecated'    | 'deprecated'                               | 'deprecated'
    'uniqueId'      | 'method1'                                  | 'uniqueId'
    'produces'      | singleton('app/json')                      | 'produces'
    'consumes'      | singleton('app/json')                      | 'consumes'
    'protocols'     | singleton('https')                         | 'protocol'
    'tags'          | singleton('tag')                           | 'tags'
    'position'      | 1                                          | 'position'
    'hidden'        | true                                       | 'hidden'
    'extensions'    | [Mock(VendorExtension)]                    | 'vendorExtensions'
    'hidden'        | true                                       | 'hidden'
    'parameters'    | [new ParameterBuilder().name("p").build()] | 'parameters'
  }

  def "Unique id takes into account the codegen method name stem"() {
    given:
    def sut = new OperationBuilder(nameGenerator)
    when:
    nameGenerator.startingWith("abc") >> "abcmethod1"
    sut.codegenMethodNameStem("abc")
    and:
    def built = sut.build()
    then:
    built.uniqueId == "abcmethod1"
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
    def sut = new OperationBuilder(nameGenerator)
    when:
    nameGenerator.startingWith(_) >> "method1"
    sut."$builderMethod"(value)
    sut."$builderMethod"(null)
    and:
    def built = sut.build()
    then:
    built."$property" == value

    where:
    builderMethod   | value                                      | property
    'method'        | HttpMethod.PUT                             | 'method'
    'summary'       | 'method1 summary'                          | 'summary'
    'notes'         | 'method1 notes'                            | 'notes'
    'responseModel' | new ModelRef('string')                     | 'responseModel'
    'deprecated'    | 'deprecated'                               | 'deprecated'
    'uniqueId'      | 'method1'                                  | 'uniqueId'
    'produces'      | singleton('app/json')                      | 'produces'
    'consumes'      | singleton('app/json')                      | 'consumes'
    'protocols'     | singleton('https')                         | 'protocol'
    'tags'          | new HashSet<>()                            | 'tags'
    'parameters'    | [new ParameterBuilder().name("p").build()] | 'parameters'
  }

  def "Operation authorizations are converted to a map by type"() {
    given:
    def sut = new OperationBuilder(nameGenerator)
    def mockAuth1 = Mock(SecurityReference)
    def mockAuth2 = Mock(SecurityReference)
    and:
    mockAuth1.reference >> "auth1"
    mockAuth1.scopes >> []
    mockAuth2.reference >> "auth2"
    mockAuth2.scopes >> []
    and:
    def authorizations = [mockAuth1, mockAuth2]
    when:
    sut.authorizations(authorizations)
    and:
    def built = sut.build()
    then:
    built.securityReferences.containsKey("auth1")
    built.securityReferences.get("auth1") == mockAuth1.scopes
    built.securityReferences.containsKey("auth2")
    built.securityReferences.get("auth2") == mockAuth2.scopes

  }
}
