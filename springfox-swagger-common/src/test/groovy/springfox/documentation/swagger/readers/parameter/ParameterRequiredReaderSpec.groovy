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

import com.google.common.base.Optional
import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class ParameterRequiredReaderSpec extends DocumentationContextSpec implements ApiParamAnnotationSupport {

  def "parameters required using default reader"() {
    given:
      def parameterContext = setupParameterContext(paramAnnotation)
    when:
      def operationCommand = stubbedParamBuilder(paramAnnotation);
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isRequired() == expected
    where:
      paramAnnotation             | expected
      apiParamWithRequired(false) | false
      apiParamWithRequired(true)  | true
      null                        | false
  }

  def setupParameterContext(paramAnnotation) {
    MethodParameter methodParameter = Mock(MethodParameter)
    methodParameter.getParameterAnnotation(ApiParam) >> paramAnnotation
    methodParameter.getParameterType() >> Object.class
    def resolvedMethodParameter = Mock(ResolvedMethodParameter)
    resolvedMethodParameter.methodParameter >> methodParameter
    def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
    new ParameterContext(
        resolvedMethodParameter,
        new ParameterBuilder(),
        context(),
        genericNamingStrategy,
        Mock(OperationContext))
  }

  def stubbedParamBuilder(ApiParam apiParamAnnotation) {
    new ApiParamParameterBuilder() {
      @Override
      def Optional<ApiParam> findApiParam(MethodParameter methodParameter) {
        Optional.fromNullable(apiParamAnnotation)
      }
    }
  }
}
