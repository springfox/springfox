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

package springfox.documentation.swagger.schema

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import org.joda.time.LocalDate
import spock.lang.Specification
import springfox.documentation.schema.mixins.ConfiguredObjectMapperSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.TypeWithAnnotatedGettersAndSetters
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

@Mixin(ConfiguredObjectMapperSupport)
class ApiModelPropertyPropertyBuilderPluginSpec extends Specification {
  BeanDescription beanDescription

  def setup() {
    beanDescription = beanDescription(TypeWithAnnotatedGettersAndSetters)
  }

  def "ApiModelProperty annotated models get enriched with additional info given a bean property" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }, new TypeResolver(),
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == allowableValues
      enriched.isRequired() == required
      enriched.description == description
      !enriched.isHidden()
    where:
      property    | required | description              | allowableValues
      "intProp"   | true     | "int Property Field"     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

  def "ApiModelProperty annotated models get enriched with additional info given an annotated element" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }.getter.annotated, new TypeResolver(),
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == allowableValues
      enriched.isRequired() == required
      enriched.description == description
      !enriched.isHidden()
    where:
      property    | required | description              | allowableValues
      "intProp"   | null     | null                     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

  def "ApiModelProperties marked as hidden properties are respected" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }.getter.annotated, new TypeResolver(),
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == allowableValues
      enriched.isRequired() == required
      enriched.description == description
      enriched.isHidden()
    where:
      property    | required | description              | allowableValues
      "hiddenProp"| false    | ""                       | null
  }

  def "Supports ApiModelProperty annotated models with dataType overrides" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()

      def resolver = new TypeResolver()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }.getter.annotated, resolver,
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == null
      !enriched.isRequired()
      enriched.description == ""
      !enriched.isHidden()
      enriched.type.getErasedType() == dataType
    where:
      property          | dataType
      "validOverride"    | String
      "invalidOverride"  | Object
  }

  def "Supports ApiModelProperty annotated models with dataType overrides but protects specific types" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()

      def resolver = new TypeResolver()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }.getter.annotated, resolver,
              DocumentationType.SWAGGER_12)
    when:
      context.builder.type(resolver.resolve(LocalDate))
    and:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == null
      !enriched.isRequired()
      enriched.description == ""
      !enriched.isHidden()
      enriched.type.getErasedType() == dataType
    where:
      property          | dataType
      "validOverride"    | String
      "invalidOverride"  | LocalDate
  }

  BeanDescription beanDescription(Class<TypeWithAnnotatedGettersAndSetters> clazz) {
    def objectMapper = new ObjectMapper()
    objectMapper.getDeserializationConfig()
            .introspect(TypeFactory.defaultInstance().constructType(clazz))
  }

}
