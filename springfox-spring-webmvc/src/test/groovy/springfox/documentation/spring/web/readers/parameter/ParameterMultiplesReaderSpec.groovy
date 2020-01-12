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

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiParam
import spock.lang.Unroll
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class ParameterMultiplesReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        ModelProviderForServiceSupport  {

  def sut = new ParameterMultiplesReader();
  
  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "param multiples #apiParamAnnotation && #paramType for default reader"() {
    given:
      ResolvedType resolvedType = paramType != null ? new TypeResolver().resolve(paramType) : null
      ResolvedMethodParameter resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", [apiParamAnnotation], resolvedType)
      ParameterContext parameterContext = new ParameterContext(
          resolvedMethodParameter
          ,
          documentationContext(),
          Mock(GenericTypeNamingStrategy),
          Mock(OperationContext))

    when:
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isAllowMultiple() == expected
    where:
      apiParamAnnotation                        | paramType                       | expected
      [allowMultiple: { -> true }] as ApiParam  | String.class                    | false
      [allowMultiple: { -> true }] as ApiParam  | String[].class                  | true
      [allowMultiple: { -> false }] as ApiParam | String[].class                  | true
      [allowMultiple: { -> false }] as ApiParam | DummyClass.BusinessType[].class | true
      null                                      | String[].class                  | true
      null                                      | List.class                      | true
      null                                      | Collection.class                | true
      null                                      | Set.class                       | true
      null                                      | Vector.class                    | true
      null                                      | Object[].class                  | true
      null                                      | Integer.class                   | false
      null                                      | Iterable.class                  | true
  }


}
