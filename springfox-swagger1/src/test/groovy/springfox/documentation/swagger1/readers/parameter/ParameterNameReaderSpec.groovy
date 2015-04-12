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

import com.google.common.base.Optional
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.swagger.readers.parameter.ParameterAnnotationReader

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterNameReaderSpec extends DocumentationContextSpec {
//   @Unroll
  def "param required"() {
    given:
      ParameterAnnotationReader annotations = Mock(ParameterAnnotationReader)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      annotations.fromHierarchy(methodParameter, ApiParam.class) >> { return Optional.absent() }
      annotations.getParentInterfaces(methodParameter) >> []
      methodParameter.getParameterName() >> "default"
      methodParameter.getParameterAnnotations() >> [annotation]
      methodParameter.getParameterIndex() >> 0
      ResolvedMethodParameter resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), genericNamingStrategy)
    when:
      def operationCommand = new springfox.documentation.spring.web.readers.parameter.ParameterNameReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().name == expected
    where:
      annotation                                       | expected
      [value: { -> "pathV" }] as PathVariable          | "pathV"
      [value: { -> "pathModelAtt" }] as ModelAttribute | "pathModelAtt"
      [value: { -> "reqHeaderAtt" }] as RequestHeader  | "reqHeaderAtt"
      [value: { -> "RequestParam" }] as RequestParam   | "RequestParam"
      null                                             | "default"
  }
}
