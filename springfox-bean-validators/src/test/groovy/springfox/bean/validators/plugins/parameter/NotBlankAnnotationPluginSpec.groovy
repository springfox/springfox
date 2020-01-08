/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.ResolvedType
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext

class NotBlankAnnotationPluginSpec extends Specification implements AnnotationsSupport {
  def "Always supported" () {
    expect:
      new NotBlankAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@NotBlank annotations are reflected in the model #propertyName that are AnnotatedElements"()  {
    given:
      def sut = new NotBlankAnnotationPlugin()
      def resolvedMethodParameter =
        new ResolvedMethodParameter(0, "", [annotation], Mock(ResolvedType))
      ParameterContext context = new ParameterContext(
          resolvedMethodParameter,
          Mock(DocumentationContext),
          Mock(GenericTypeNamingStrategy),
          Mock(OperationContext))

    when:
      sut.apply(context)
      def property = context.parameterBuilder().build()
    then:
      property.required == required
    where:
      annotationDescription | required  | annotation
      "none"                | false     | null
      "@NotBlank"           | true      | notBlank()
  }
}
