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

import com.fasterxml.classmate.TypeResolver
import org.springframework.mock.env.MockEnvironment
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class ParameterRequiredReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        ApiParamAnnotationSupport {
  def descriptions = new DescriptionResolver(new MockEnvironment())
  def enumTypeDeterminer = new JacksonEnumTypeDeterminer()

  def "parameters required using default reader"() {
    given:
    def parameterContext = setupParameterContext(paramAnnotation)

    when:
    def operationCommand = stubbedParamBuilder()
    operationCommand.apply(parameterContext)

    then:
    parameterContext.parameterBuilder().build().isRequired() == expected

    where:
    paramAnnotation             | expected
    apiParamWithRequired(false) | false
    apiParamWithRequired(true)  | true
    null                        | false
  }

  def "parameters hidden using default reader"() {
    given:
    def parameterContext = setupParameterContext(paramAnnotation)

    when:
    def operationCommand = stubbedParamBuilder()
    operationCommand.apply(parameterContext)

    then:
    parameterContext.parameterBuilder().build().isHidden() == expected

    where:
    paramAnnotation           | expected
    apiParamWithHidden(false) | false
    apiParamWithHidden(true)  | true
    null                      | false
  }

  def setupParameterContext(paramAnnotation) {
    def resolvedMethodParameter = new ResolvedMethodParameter(
        0,
        "",
        [paramAnnotation],
        new TypeResolver().resolve(Object.class))
    def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
    new ParameterContext(
        resolvedMethodParameter
        ,
        documentationContext(),
        genericNamingStrategy,
        Mock(OperationContext))
  }

  def stubbedParamBuilder() {
    new ApiParamParameterBuilder(descriptions, enumTypeDeterminer)
  }
}
