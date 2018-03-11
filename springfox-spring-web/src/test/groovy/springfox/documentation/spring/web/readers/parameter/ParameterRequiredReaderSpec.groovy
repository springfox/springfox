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

package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.springframework.core.MethodParameter
import org.springframework.mock.env.MockEnvironment
import org.springframework.web.bind.annotation.PathVariable
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.lang.annotation.Annotation

import static org.springframework.web.bind.annotation.ValueConstants.*

@Mixin([RequestMappingSupport])
class ParameterRequiredReaderSpec extends DocumentationContextSpec implements ParameterAnnotationSupport {
  @Shared
  def description = new DescriptionResolver(new MockEnvironment())

  @Unroll
  def "parameters required paramAnnotations using default reader"() {
    given:
    MethodParameter methodParameter = Mock(MethodParameter)
    methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
    methodParameter.getParameterType() >> Object.class
    methodParameter.getMethodAnnotation(PathVariable.class) >> paramAnnotations.find { it instanceof PathVariable }

    and:
    def operation = Mock(OperationContext)
    operation.requestMappingPattern() >> requestPattern

    and:
    def resolvedMethodParameter =
        new ResolvedMethodParameter(
            0,
            "",
            paramAnnotations,
            new TypeResolver().resolve(Object.class))
    ParameterContext parameterContext =
        new ParameterContext(
            resolvedMethodParameter,
            new ParameterBuilder(),
            context(),
            Mock(GenericTypeNamingStrategy),
            operation)

    when:
    def operationCommand = new ParameterRequiredReader(description)
    operationCommand.apply(parameterContext)

    then:
    parameterContext.parameterBuilder().build().isRequired() == expected

    where:
    paramAnnotations                                        | requestPattern           | expected
    [apiParam(false), pathVariableRequired()]               | "/path/{required-param}" | true
    [apiParam(true), pathVariableRequired()]                | "/path/{required-param}" | true
    [apiParam(false), pathVariableOptional()]               | "/path/{optional-param}" | true
    [apiParam(false), pathVariableOptional()]               | "/path"                  | false
    [apiParam(true), pathVariableOptional()]                | "/path/{optional-param}" | true
    [apiParam(true), pathVariableOptional()]                | "/path"                  | false
    [apiParam(false), requestHeader(false, "", "")]         | "/path"                  | false
    [requestHeader(true, "", "")]                           | "/path"                  | true
    [requestHeader(false, "", "")]                          | "/path"                  | false
    [apiParam(true)]                                        | "/path"                  | false
    [apiParam(false)]                                       | "/path"                  | false
    [requestParam(true, "", DEFAULT_NONE)]                  | "/path"                  | true
    [requestParam(true, "", "")]                            | "/path"                  | false
    [requestParam(true, "", null)]                          | "/path"                  | false
    [requestParam(true, "", "default value")]               | "/path"                  | false
    [requestParam(false, "", DEFAULT_NONE)]                 | "/path"                  | false
    [requestParam(false, "", "")]                           | "/path"                  | false
    [requestBody(false)]                                    | "/path"                  | false
    [requestBody(true)]                                     | "/path"                  | true
    [requestPart(false, "")]                                | "/path"                  | false
    [requestPart(true, "")]                                 | "/path"                  | true
    []                                                      | "/path"                  | false
    [null]                                                  | "/path"                  | false
    [apiParam(true), requestParam(false, "", DEFAULT_NONE)] | "/path"                  | false
    [apiParam(false), requestParam(true, "", DEFAULT_NONE)] | "/path"                  | true
    [apiParam(false), requestParam(true, "", DEFAULT_NONE)] | "/path"                  | true
  }

  def pathVariableRequired() {
    pathVariable("required-param", true)
  }

  def pathVariableOptional() {
    pathVariable("optional-param", false)
  }

  def "should detect java.util.Optional parameters"() {
    given:
    def resolvedMethodParameter = new ResolvedMethodParameter(
        0,
        "",
        paramAnnotations,
        new TypeResolver().resolve(Object.class))
    ParameterContext parameterContext = new ParameterContext(
        resolvedMethodParameter,
        new ParameterBuilder(),
        context(),
        Mock(GenericTypeNamingStrategy),
        Mock(OperationContext))

    when:
    def operationCommand = new ParameterRequiredReader(description) {
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
    def sut = new ParameterRequiredReader(description)

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }
}
