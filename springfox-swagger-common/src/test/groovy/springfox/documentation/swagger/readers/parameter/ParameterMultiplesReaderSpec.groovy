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
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class ParameterMultiplesReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        ApiParamAnnotationSupport,
        ModelProviderForServiceSupport  {
  
  def descriptions = new DescriptionResolver(new MockEnvironment())

  @Unroll
  def "param multiples for swagger reader"() {
    given:
    MethodParameter methodParameter = Stub(MethodParameter)
    methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
    methodParameter.getParameterType() >> paramType
    ResolvedType resolvedType = paramType != null ? new TypeResolver().resolve(paramType) : null
    ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter("", methodParameter, resolvedType)
    def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
    ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter,
        documentationContext(), genericNamingStrategy, Mock(OperationContext))

    when:
    def operationCommand = stubbedParamBuilder();
    operationCommand.apply(parameterContext)

    then:
    parameterContext.parameterBuilder().build().isAllowMultiple() == expected

    where:
    apiParamAnnotation               | paramType                       | expected
    apiParamWithAllowMultiple(false) | String[].class                  | false
    apiParamWithAllowMultiple(false) | DummyClass.BusinessType[].class | false
    null                             | String[].class                  | false
    null                             | List.class                      | false
    null                             | Collection.class                | false
    null                             | Set.class                       | false
    null                             | Vector.class                    | false
    null                             | Object[].class                  | false
    null                             | Integer.class                   | false
    null                             | Iterable.class                  | false
  }

  def stubbedParamBuilder() {
    new ApiParamParameterBuilder(descriptions, new JacksonEnumTypeDeterminer())
  }
}
