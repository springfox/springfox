/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiParam
import org.springframework.mock.env.MockEnvironment
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.method.HandlerMethod
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class ParameterReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        ModelProviderForServiceSupport  {
  @Shared
  description = new DescriptionResolver(new MockEnvironment())
  
  def "should set basic properties based on ApiParam annotation or a sensible default"() {
    given:
      def resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", [apiParamAnnotation, reqParamAnnot], Mock(ResolvedType))
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter,
          documentationContext(), Mock(GenericTypeNamingStrategy), Mock(OperationContext))
    when:
      parameterPlugin.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
    where:
      parameterPlugin                         | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation | reqParamAnnot                          | expected
      new ParameterDefaultReader(description) | 'defaultValue' | 'none'                | 'any'             | null               | null                                   | null
      new ParameterDefaultReader(description) | 'defaultValue' | 'none'                | 'any'             | apiParam ([defaultValue: {-> 'defl' }]) | null              | null
      new ParameterDefaultReader(description) | 'defaultValue' | 'none'                | 'any'             | null               | reqParam([defaultValue: {-> 'defr' }]) | 'defr'
  }

  @Unroll
  def "should set parameter name and description correctly for #methodName"() {
    given:
      def bean = new ParamNameClazzSpecimen()
      def resolvedBeanType = new TypeResolver().resolve(ParamNameClazzSpecimen)
      HandlerMethod method = new HandlerMethod(bean, ParamNameClazzSpecimen.methods.find {it.name.equals(methodName)})
      def resolvedMethodParameter  = new ResolvedMethodParameter("someName", method.getMethodParameters().first(),
          resolvedBeanType)
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter,
          documentationContext(), Mock(GenericTypeNamingStrategy), Mock(OperationContext))
    when:
      parameterPlugin.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
      parameterContext.parameterBuilder().build().description == expected
    where:
      parameterPlugin           | resultProperty | methodName | expected
      new ParameterNameReader() | 'name'         | "method1"  | 'someName'
      new ParameterNameReader() | 'name'         | "method2"  | 'someName'
      new ParameterNameReader() | 'name'         | "method3"  | 'ArName'
      new ParameterNameReader() | 'name'         | "method4"  | 'header'
      new ParameterNameReader() | 'name'         | "method5"  | 'modelAttr'
      new ParameterNameReader() | 'name'         | "method6"  | 'pathVar'
      new ParameterNameReader() | 'name'         | "method7"  | 'partName'
  }

  class ParamNameClazzSpecimen {
    void method1(String someName) {
    }
    void method2(@ApiParam(name = "AnName") String someName) {
    }
    void method3(@RequestParam(value = "ArName") String someName) {
    }
    void method4(@RequestHeader(value = "header") String someName) {
    }
    void method5(@ModelAttribute(value = "modelAttr") String someName) {
    }
    void method6(@PathVariable(value = "pathVar") String someName) {
    }
    void method7(@RequestPart(value = "partName") String someName) {
    }
  }

  def "ParameterNameReader supports all documentationTypes"() {
    given:
      def sut = new ParameterNameReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "ParameterDefaultReader should work with any documentationType"() {
    given:
      def sut = new ParameterDefaultReader(description)
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "ParameterTypeReader should work with any documentationType"() {
    given:
      def sut = new ParameterTypeReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  private ApiParam apiParam(Map closureMap) {
      closureMap as ApiParam
   }

   private RequestParam reqParam(Map closureMap) {
      closureMap as RequestParam
   }
}
