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

package springfox.documentation.swagger.readers.parameter
import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestParam
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

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
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), genericNamingStrategy, Mock(OperationContext))
    when:
      sut.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      sut                              | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation                      | reqParamAnnot | expected
      new ParameterDescriptionReader() | 'description'  | 'getParameterName'    | 'someName'        | null                                    | null          | 'someName'
      new ParameterDescriptionReader() | 'description'  | 'none'                | 'any'             | apiParam([value: { -> 'AnDesc' }])      | null          | 'AnDesc'
      swaggerDefaultReader()           | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: { -> 'defl' }]) | null          | 'defl'
      new ParameterAccessReader()      | 'paramAccess'  | 'none'                | 'any'             | apiParam([access: { -> 'myAccess' }])   | null          | 'myAccess'
  }


  ParameterDefaultReader swaggerDefaultReader() {
    new ParameterDefaultReader()
  }


  private ApiParam apiParam(Map closureMap) {
    closureMap as ApiParam
  }

  def "supports all swagger types" () {
    given:
      ParameterRequiredReader sut = new ParameterRequiredReader()
    expect:
      sut.supports(documentationType)
    where:
      documentationType << [DocumentationType.SWAGGER_12, DocumentationType.SWAGGER_2]
  }

}
