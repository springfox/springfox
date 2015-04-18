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

import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestParam
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.service.ResolvedMethodParameter

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterReaderSpec extends DocumentationContextSpec {
   @Unroll("property #resultProperty expected: #expected")
   def "should set basic properties based on ApiParam annotation or a sensible default"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterAnnotation(RequestParam.class) >> reqParamAnnot
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation, reqParamAnnot]
      methodParameter."$springParameterMethod"() >> methodReturnValue
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), Mock(GenericTypeNamingStrategy), Mock(OperationContext))
    when:
      parameterPlugin.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
    where:
      parameterPlugin                     | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation                     | reqParamAnnot                          | expected
      new ParameterNameReader()           | 'name'         | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | apiParam ([name: {-> 'AnName' }])      | null                                   | 'param0'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | null                                   | reqParam([value: {-> 'ArName' }])      | 'ArName'
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | null                                   | null
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: {-> 'defl' }]) | null                                   | null
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | reqParam([defaultValue: {-> 'defr' }]) | 'defr'
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
      def sut = new ParameterDefaultReader()
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
