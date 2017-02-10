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

import com.google.common.collect.ImmutableSet
import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ValueConstants
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.OperationModelContextsBuilder
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.lang.annotation.Annotation

@Mixin([RequestMappingSupport])
class ParameterRequiredReaderSpec extends DocumentationContextSpec implements ParameterAnnotationSupport {

  @Unroll
  def "parameters required #paramAnnotations using default reader"() {
    given:
      MethodParameter methodParameter = Mock(MethodParameter)
      methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
      methodParameter.getParameterType() >> Object.class
      methodParameter.getMethodAnnotation(PathVariable.class) >> paramAnnotations.find { it instanceof PathVariable }
      def resolvedMethodParameter =
          new ResolvedMethodParameter(0, false, "", paramAnnotations, new TypeResolver().resolve(Object.class))
      OperationModelContextsBuilder operationModelContextsBuilder =
          new OperationModelContextsBuilder(DocumentationType.SWAGGER_12, Mock(AlternateTypeProvider), Mock(GenericTypeNamingStrategy),
              ImmutableSet.builder().build())
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), Mock(GenericTypeNamingStrategy), Mock(OperationContext),
          operationModelContextsBuilder.inputParam(new TypeResolver().resolve(Object.class), resolvedMethodParameter))
    when:
      def operationCommand = new ParameterRequiredReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isRequired() == expected
    where:
      paramAnnotations                                                      | expected
      [[required: { -> false }] as ApiParam, pathVariable(false)]           | true
      [[required: { -> false }] as ApiParam, requestHeader(false, "", "")]  | false
      [requestHeader(true, "", "")]                                         | true
      [requestHeader(false, "", "")]                                        | false
      [[required: { -> true }] as ApiParam]                                 | false
      [[required: { -> false }] as ApiParam]                                | false
      [requestParam(true, "", ValueConstants.DEFAULT_NONE)]                 | true
      [requestParam(true, "", "")]                                          | true
      [requestParam(true, "", null)]                                        | true
      [requestParam(true, "", "default value")]                             | false
      [requestParam(false, "", ValueConstants.DEFAULT_NONE)]                | false
      [requestBody(false)]                                                  | false
      [requestBody(true)]                                                   | true
      [requestPart(false, "")]                                              | false
      [requestPart(true, "")]                                               | true
      []                                                                    | false
      [null]                                                                | false
      [[required: { -> true }] as ApiParam, requestParam(false, "", ValueConstants.DEFAULT_NONE)] | false
      [[required: { -> false }] as ApiParam, requestParam(true, "", ValueConstants.DEFAULT_NONE)] | true
      [[required: { -> false }] as ApiParam, requestParam(true, "", ValueConstants.DEFAULT_NONE)] | true
  }

  def "should detect java.util.Optional parameters"() {
    given:
      def resolvedMethodParameter = new ResolvedMethodParameter(
        0,
        false,
        "",
        paramAnnotations,
        new TypeResolver().resolve(Object.class))
      OperationModelContextsBuilder operationModelContextsBuilder =
          new OperationModelContextsBuilder(DocumentationType.SWAGGER_12, Mock(AlternateTypeProvider), Mock(GenericTypeNamingStrategy),
              ImmutableSet.builder().build())
      ParameterContext parameterContext = new ParameterContext(
          resolvedMethodParameter,
          new ParameterBuilder(),
          context(),
          Mock(GenericTypeNamingStrategy),
          Mock(OperationContext),
          operationModelContextsBuilder.inputParam(new TypeResolver().resolve(Object.class), resolvedMethodParameter))

    when:
      def operationCommand = new ParameterRequiredReader() {
        @Override
        def boolean isOptional(ResolvedMethodParameter input) {
          true
        }
      }
      operationCommand.apply(parameterContext)
    then:
      !parameterContext.parameterBuilder().build().isRequired()
    where:
      paramAnnotations << [
              [requestHeader(true, "", "")],
              [requestHeader(false, "", "")],
              [requestParam(true, "", "")],
              [requestParam(false, "", "")]
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
