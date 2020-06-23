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

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor

import javax.validation.constraints.Pattern

class ExpandedParameterPatternAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  @Shared
  def resolver = new TypeResolver()

  def "Always supported"() {
    expect:
    new ExpandedParameterPatternAnnotationPlugin().supports(types)

    where:
    types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Pattern annotations are reflected in the model properties that are AnnotatedElements for #fieldName"() {
    given:
    def sut = new ExpandedParameterPatternAnnotationPlugin()
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [named(Subject, fieldName).rawMember],
            resolver.resolve(Subject),
            fieldName),
        DocumentationType.SWAGGER_12,
        new ParameterBuilder(),
        new RequestParameterBuilder())

    when:
    sut.apply(context)
    def property = context.parameterBuilder.build()

    then:
    property?.pattern == annotation?.regexp()

    where:
    fieldName      | annotation
    "noAnnotation" | null
    "annotated"    | pattern("[a-zA-Z0-9_]")
  }

  class Subject {
    String noAnnotation
    @Pattern(regexp = "[a-zA-Z0-9_]")
    String annotated
  }
}
