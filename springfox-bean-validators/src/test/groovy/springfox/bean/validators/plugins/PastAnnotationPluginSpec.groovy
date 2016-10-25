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
package springfox.bean.validators.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.models.PastTestModel
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class PastAnnotationPluginSpec extends Specification {
  def "Always supported" () {
    expect:
      new PastAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Past annotations are reflected in the model properties that are AnnotatedElements"()  {
    given:
      def sut = new PastAnnotationPlugin()
      def element = PastTestModel.getDeclaredField(propertyName)
      def context = new ModelPropertyContext(
          new ModelPropertyBuilder(),
          element,
          new TypeResolver(),
          DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
      def property = context.builder.build()
    then:
      property.isRequired() == required
    where:
      propertyName                  | required
      "pastString"                  | true
      "pastGetter"                  | false
      "string"                      | false

  }

  @Unroll
  def "@Past annotations are reflected in the model properties that are BeanPropertyDefinitions"()  {
    given:
      def sut = new PastAnnotationPlugin()
      def beanProperty = beanProperty(propertyName)
      def context = new ModelPropertyContext(
          new ModelPropertyBuilder(),
          beanProperty,
          new TypeResolver(),
          DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
      def property = context.builder.build()
    then:
      property.isRequired() == required
    where:
      propertyName                    | required
      "pastString"                    | true
      "pastGetter"                    | true
      "string"                        | false

  }

  def beanProperty(property) {
    def mapper = new ObjectMapper()
    mapper
        .serializationConfig
        .introspect(TypeFactory.defaultInstance().constructType(PastTestModel))
        .findProperties()
        .find { p -> property.equals(p.name) };
  }

}
