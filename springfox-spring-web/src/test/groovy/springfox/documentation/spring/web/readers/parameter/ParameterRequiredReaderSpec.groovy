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

package springfox.documentation.spring.web.readers.parameter

import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.lang.annotation.Annotation

@Mixin([RequestMappingSupport])
class ParameterRequiredReaderSpec extends DocumentationContextSpec {

  def "parameters required using default reader"() {
    given:
      MethodParameter methodParameter = Mock(MethodParameter)
      methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
      methodParameter.getParameterType() >> Object.class
      methodParameter.getMethodAnnotation(PathVariable.class) >> paramAnnotations.find { it instanceof PathVariable }
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), Mock(GenericTypeNamingStrategy), Mock(OperationContext))
    when:
      def operationCommand = new ParameterRequiredReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isRequired() == expected
    where:
      paramAnnotations                                                                  | expected
      [[required: { -> false }] as ApiParam, [required: { -> false }] as PathVariable]  | true
      [[required: { -> false }] as ApiParam, [required: { -> false }] as RequestHeader] | false
      [[required: { -> true }] as RequestHeader]                                        | true
      [[required: { -> false }] as RequestHeader]                                       | false
      [[required: { -> true }] as ApiParam]                                             | false
      [[required: { -> false }] as ApiParam]                                            | false
      [[required: { -> true }] as RequestParam]                                         | true
      [[required: { -> false }] as RequestParam]                                        | false
      [[required: { -> true }] as ApiParam, [required: { -> false }] as RequestParam]   | false
      [[required: { -> false }] as ApiParam, [required: { -> true }] as RequestParam]   | true
      [[required: { -> false }] as RequestBody]                                         | false
      [[required: { -> true }] as RequestBody]                                          | true
      [[required: { -> false }] as ApiParam, [required: { -> true }] as RequestParam]   | true
      []                                                                                | false
      [null]                                                                            | false
  }

  def "should detect java.util.Optional parameters"() {
    given:
      MethodParameter methodParameter = Mock(MethodParameter)
      methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      Class<?> fakeOptionalClass = new FakeOptional().class
      fakeOptionalClass.name = "java.util.Optional"
      methodParameter.getParameterType() >> fakeOptionalClass
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), Mock(GenericTypeNamingStrategy), Mock(OperationContext))

    when:
      def operationCommand = new ParameterRequiredReader();
      operationCommand.apply(parameterContext)
    then:
      !parameterContext.parameterBuilder().build().isRequired()
    where:
      paramAnnotations << [
              [[required: { -> true }] as RequestHeader],
              [[required: { -> false }] as RequestHeader],
              [[required: { -> true }] as RequestParam],
              [[required: { -> false }] as RequestParam],
      ]
  }

  def "Supports all documentation types"() {
    given:
      def sut = new ParameterRequiredReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }
}

class FakeOptional {}
