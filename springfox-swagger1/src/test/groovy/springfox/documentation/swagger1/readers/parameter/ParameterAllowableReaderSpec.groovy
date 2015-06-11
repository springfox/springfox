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

package springfox.documentation.swagger1.readers.parameter

import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.swagger.readers.parameter.ParameterAllowableReader

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterAllowableReaderSpec extends DocumentationContextSpec {

  def "enum types"() {
    given:
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 0)
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), genericNamingStrategy, Mock(OperationContext))

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableListValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableListValues
    then:
      allowableValues != null
      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == ["PRODUCT", "SERVICE"]
    where:
      handlerMethod                                                                    | expected
      dummyHandlerMethod('methodWithSingleEnum', DummyClass.BusinessType.class)        | AllowableListValues
      dummyHandlerMethod('methodWithSingleEnumArray', DummyClass.BusinessType[].class) | AllowableListValues
  }

  @Unroll
  def "Api annotation with list type"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
          context(), genericNamingStrategy, Mock(OperationContext))

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableListValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableListValues
    then:
      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == expected
    where:
      apiParamAnnotation                                | expected
      [allowableValues: { -> "1, 2" }] as ApiParam      | ['1', '2']
      [allowableValues: { -> "1,2,3,4" }] as ApiParam   | ['1', '2', '3', '4']
      [allowableValues: { -> "1,2,   ,4" }] as ApiParam | ['1', '2', '4']
      [allowableValues: { -> "1" }] as ApiParam         | ['1']
  }

  @Unroll("Range: #min | #max")
  def "Api annotation with ranges"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter,
          new ParameterBuilder(), context(), genericNamingStrategy, Mock(OperationContext))

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableRangeValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableRangeValues
    then:
      allowableValues.getMin() == min as String
      allowableValues.getMax() == max as String
    where:
      apiParamAnnotation                                                         | min | max
      [allowableValues: { -> "range[1,5]" }] as ApiParam                         | 1   | 5
      [allowableValues: { -> "range[1,1]" }] as ApiParam                         | 1   | 1
      [allowableValues: { -> "range[2," + Integer.MAX_VALUE + "]" }] as ApiParam | 2   | Integer.MAX_VALUE
  }
}
