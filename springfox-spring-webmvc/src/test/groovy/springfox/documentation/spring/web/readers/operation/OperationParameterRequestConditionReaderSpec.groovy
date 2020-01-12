/*
 *
 *  Copyright 2016 the original author or authors.
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

package springfox.documentation.spring.web.readers.operation

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.service.Parameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationParameterRequestConditionReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {

  OperationParameterRequestConditionReader sut = new OperationParameterRequestConditionReader(new TypeResolver())
  def "Should read a parameter given a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=testValue")
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition])
      OperationContext operationContext =
          operationContext(documentationContext(), handlerMethod, 0, requestMappingInfo)
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    and:
      Parameter parameter = operation.parameters[0]
      assert parameter."$property" == expectedValue

    where:
      property        | expectedValue
      'name'          | 'test'
      'defaultValue'  | 'testValue'
      'description'   | null
      'required'      | true
      'allowMultiple' | false
      'paramType'     | "query"

  }

  def "Should ignore a negated parameter in a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("!test")
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition])
      OperationContext operationContext =
          operationContext(documentationContext(), handlerMethod, 0, requestMappingInfo)

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      0 == operation.parameters.size()

  }

  def "Should ignore a parameter request condition expression that is already present in the parameters"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=testValue", "test=3")
      def requestMappingInfo = requestMappingInfo('/parameter-conditions', ["paramsCondition": paramCondition])
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod, 0, requestMappingInfo)
    when:
      sut.apply(operationContext)

    then:
      1 == operationContext.operationBuilder().build().parameters.size()

  }
}
