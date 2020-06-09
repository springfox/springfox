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
package springfox.bean.validators.plugins.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.models.SizeTestModel
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.PropertySpecificationBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class SizeAnnotationPluginSpec extends Specification {
  def "Always supported" () {
    expect:
      new SizeAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Size annotations are reflected in the model #propertyName that are AnnotatedElements"()  {
    given:
      def sut = new SizeAnnotationPlugin()
      def element = SizeTestModel.getDeclaredField(propertyName)
      def context = new ModelPropertyContext(
          new ModelPropertyBuilder(), new PropertySpecificationBuilder(propertyName),
          element,
          new TypeResolver(),
          Mock(ModelContext))
    when:
      sut.apply(context)
      def property = context.builder.build()
    then:
      def range = property.allowableValues as AllowableRangeValues
      range?.max == expectedMax
      range?.min == expectedMin
    where:
      propertyName      | expectedMin                   | expectedMax
      "noAnnotation"    | null                          | null
      "defaultSize"     | "0"                           | Integer.MAX_VALUE.toString()
      "belowZero"       | "0"                           | "10"
      "aboveMax"        | "10"                          | "0"
      "inverted"        | Integer.MAX_VALUE.toString()  | "0"
      "bothNegative"    | "0"                           | "0"
      "bothZero"        | "0"                           | "0"
      "bothMax"         | Integer.MAX_VALUE.toString()  | Integer.MAX_VALUE.toString()
  }

}
