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
import springfox.documentation.common.SpringVersion
import springfox.documentation.common.Version
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

class ParameterRequiredReaderSpec
    extends DocumentationContextSpec
    implements ParameterAnnotationSupport, RequestMappingSupport {
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
            resolvedMethodParameter
            ,
            documentationContext(),
            Mock(GenericTypeNamingStrategy),
            operation)
    and:
    def springVersion = Mock(SpringVersion.class);
    springVersion.getVersion() >> Version.parse(version)

    when:
    def operationCommand = new ParameterRequiredReader(description, springVersion)
    operationCommand.apply(parameterContext)

    then:
    parameterContext.parameterBuilder().build().isRequired() == expected

    where:
    paramAnnotations                                        | version         | requestPattern           | expected
    [apiParam(false), pathVariableRequired()]               | "4.3.3.RELEASE" | "/path/{required-param}" | true
    [apiParam(false), pathVariableRequired()]               | "4.2.0.RELEASE" | "/path/{required-param}" | true
    [apiParam(true), pathVariableRequired()]                | "4.3.3.RELEASE" | "/path/{required-param}" | true
    [apiParam(true), pathVariableRequired()]                | "4.2.0.RELEASE" | "/path/{required-param}" | true
    [apiParam(false), pathVariableOptional()]               | "4.3.3.RELEASE" | "/path/{optional-param}" | true
    [apiParam(false), pathVariableOptional()]               | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(true), pathVariableOptional()]                | "4.3.3.RELEASE" | "/path/{optional-param}" | true
    [apiParam(true), pathVariableOptional()]                | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(false), requestHeader(false, "", "")]         | "4.3.3.RELEASE" | "/path"                  | false
    [requestHeader(true, "", "")]                           | "4.3.3.RELEASE" | "/path"                  | true
    [requestHeader(false, "", "")]                          | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(true)]                                        | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(false)]                                       | "4.3.3.RELEASE" | "/path"                  | false
    [requestParam(true, "", DEFAULT_NONE)]                  | "4.3.3.RELEASE" | "/path"                  | true
    [requestParam(true, "", "")]                            | "4.3.3.RELEASE" | "/path"                  | false
    [requestParam(true, "", null)]                          | "4.3.3.RELEASE" | "/path"                  | false
    [requestParam(true, "", "default value")]               | "4.3.3.RELEASE" | "/path"                  | false
    [requestParam(false, "", DEFAULT_NONE)]                 | "4.3.3.RELEASE" | "/path"                  | false
    [requestParam(false, "", "")]                           | "4.3.3.RELEASE" | "/path"                  | false
    [requestBody(false)]                                    | "4.3.3.RELEASE" | "/path"                  | false
    [requestBody(true)]                                     | "4.3.3.RELEASE" | "/path"                  | true
    [requestPart(false, "")]                                | "4.3.3.RELEASE" | "/path"                  | false
    [requestPart(true, "")]                                 | "4.3.3.RELEASE" | "/path"                  | true
    []                                                      | "4.3.3.RELEASE" | "/path"                  | false
    [null]                                                  | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(true), requestParam(false, "", DEFAULT_NONE)] | "4.3.3.RELEASE" | "/path"                  | false
    [apiParam(false), requestParam(true, "", DEFAULT_NONE)] | "4.3.3.RELEASE" | "/path"                  | true
    [apiParam(false), requestParam(true, "", DEFAULT_NONE)] | "4.3.3.RELEASE" | "/path"                  | true
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
        resolvedMethodParameter
        ,
        documentationContext(),
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
