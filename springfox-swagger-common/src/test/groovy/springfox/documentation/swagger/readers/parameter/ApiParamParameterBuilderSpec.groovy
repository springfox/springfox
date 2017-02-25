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

package springfox.documentation.swagger.readers.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.mock.env.MockEnvironment
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ApiParamParameterBuilderSpec extends DocumentationContextSpec implements ApiParamAnnotationSupport {

  def "enum types"() {
    given:
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 0)
      def resolvedMethodParameter = new ResolvedMethodParameter("default", methodParameter,
          new TypeResolver().resolve(handlerMethod.methodParameters[0].getParameterType()))
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(
          resolvedMethodParameter,
          new ParameterBuilder(),
          context(),
          genericNamingStrategy,
          Mock(OperationContext))

    when:
      ApiParamParameterBuilder operationCommand = new ApiParamParameterBuilder();
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
      def resolvedMethodParameter = new ResolvedMethodParameter(0, "", [apiParamAnnotation], stubbedResolvedType())
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
          new ParameterContext(
              resolvedMethodParameter,
              new ParameterBuilder(),
              context(),
              genericNamingStrategy,
              Mock(OperationContext))

    when:
      ApiParamParameterBuilder operationCommand = stubbedParamBuilder(apiParamAnnotation)
      operationCommand.apply(parameterContext)
      AllowableListValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableListValues
    then:
      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == expected
    where:
      apiParamAnnotation                        | expected
      apiParamWithAllowableValues("1, 2")       | ['1', '2']
      apiParamWithAllowableValues("1,2,3,4")    | ['1', '2', '3', '4']
      apiParamWithAllowableValues("1,2,   ,4")  | ['1', '2', '4']
      apiParamWithAllowableValues("1")          | ['1']
  }

  @Unroll("Range: #min | #max")
  def "Api annotation with ranges"() {
    given:
      def resolvedMethodParameter = new ResolvedMethodParameter(0, "", [apiParamAnnotation], stubbedResolvedType())
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(
          resolvedMethodParameter,
          new ParameterBuilder(),
          context(),
          genericNamingStrategy,
          Mock(OperationContext))

    when:
      ApiParamParameterBuilder operationCommand = stubbedParamBuilder(apiParamAnnotation);
      operationCommand.apply(parameterContext)
      AllowableRangeValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableRangeValues
    then:
      allowableValues.getMin() == min as String
      allowableValues.getMax() == max as String
    where:
      apiParamAnnotation                                                | min | max
      apiParamWithAllowableValues("range[1,5]")                         | 1   | 5
      apiParamWithAllowableValues("range[1,1]")                         | 1   | 1
      apiParamWithAllowableValues("range[2," + Integer.MAX_VALUE + "]") | 2   | Integer.MAX_VALUE
  }

  def "supports all swagger types" () {
    given:
      ApiParamParameterBuilder sut = new ApiParamParameterBuilder()
    expect:
      sut.supports(documentationType)
    where:
      documentationType << [DocumentationType.SWAGGER_12, DocumentationType.SWAGGER_2]
  }

  def stubbedParamBuilder(ApiParam apiParamAnnotation) {
    def descriptions = new DescriptionResolver(new MockEnvironment())
    new ApiParamParameterBuilder(descriptions) {
    }
  }

  def stubbedResolvedType() {
    def resolvedType = Mock(ResolvedType)
    resolvedType.getErasedType() >> Object.class
    return resolvedType
  }
}
