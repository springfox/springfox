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

package springfox.service.model.builder
import org.springframework.http.HttpMethod
import spock.lang.Specification
import springfox.documentation.OperationNameGenerator
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ResponseMessage
import springfox.documentation.service.SecurityReference

import static com.google.common.collect.Sets.*

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
          .build()

  def "Merges response messages when new response messages are applied" () {
    given:
      sut.responseMessages(newHashSet(partialOk))
    when:
      nameGenerator.startingWith(_) >> _
      sut.responseMessages(newHashSet(fullOk))
    and:
      def operation = sut.build()
    then:
      operation.responseMessages.size() == 1
      operation.responseMessages.first().code == 200
      operation.responseMessages.first().message == "OK"
      operation.responseMessages.first().responseModel.type == "String"
      operation.responseMessages.first().responseModel.itemType == null
  }

  def "Response message builder is non-destructive" () {
    given:
      sut.responseMessages(newHashSet(fullOk))
    when:
      sut.responseMessages(newHashSet(partialOk))
    and:
      def operation = sut.build()
    then:
      operation.responseMessages.size() == 1
      operation.responseMessages.first().code == 200
      operation.responseMessages.first().message == "OK"
      operation.responseMessages.first().responseModel.type == "String"
      operation.responseMessages.first().responseModel.itemType == null
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
      builderMethod     | value                   | property
      'method'          | HttpMethod.GET          | 'method'
      'summary'         | 'method1 summary'       | 'summary'
      'notes'           | 'method1 notes'         | 'notes'
      'responseModel'    | new ModelRef('string') | 'responseModel'
      'deprecated'      | 'deprecated'            | 'deprecated'
      'uniqueId'        | 'method1'               | 'uniqueId'
      'produces'        | newHashSet('app/json')  | 'produces'
      'consumes'        | newHashSet('app/json')  | 'consumes'
      'protocols'       | newHashSet('https')     | 'protocol'
      'tags'            | newHashSet('tag')       | 'tags'
      'position'        | 1                       | 'position'
      'hidden'          | true                          | 'hidden'
      'parameters'      | [new ParameterBuilder().name("p").build()]       | 'parameters'
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
      builderMethod     | value                  | property
      'method'          | HttpMethod.PUT         | 'method'
      'summary'         | 'method1 summary'      | 'summary'
      'notes'           | 'method1 notes'        | 'notes'
      'responseModel'   | new ModelRef('string') | 'responseModel'
      'deprecated'      | 'deprecated'           | 'deprecated'
      'uniqueId'        | 'method1'              | 'uniqueId'
      'produces'        | newHashSet('app/json') | 'produces'
      'consumes'        | newHashSet('app/json') | 'consumes'
      'protocols'       | newHashSet('https')    | 'protocol'
      'tags'            | newHashSet()           | 'tags'
      'parameters'      | [new ParameterBuilder().name("p").build()] | 'parameters'
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
