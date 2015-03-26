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
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
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
              properties.find { it.name == property },
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
              properties.find { it.name == property }.getter.annotated,
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

//  def "Detects properties annotated as hidden"() {
//    given:
//      Class typeToTest = typeForTestingAnnotatedGettersAndSetter()
//      def method = accessorMethod(typeToTest, "getHiddenProp")
//      def propertyDefinition = beanPropertyDefinition(typeToTest, "getHiddenProp")
//
//      ObjectMapper mapper = new ObjectMapper()
//      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
//      def sut = new BeanModelProperty(propName, propertyDefinition, method, isGetter(method.getRawMember()),
//              new TypeResolver(), new AlternateTypeProvider())
//
//    expect:
//      sut.isHidden()
//  }

  BeanDescription beanDescription(Class<TypeWithAnnotatedGettersAndSetters> clazz) {
    def objectMapper = new ObjectMapper()
    objectMapper.getDeserializationConfig()
            .introspect(TypeFactory.defaultInstance().constructType(clazz))
  }

}
