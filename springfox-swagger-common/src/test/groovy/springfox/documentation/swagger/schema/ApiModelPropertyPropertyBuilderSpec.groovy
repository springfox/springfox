/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import org.springframework.mock.env.MockEnvironment
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.TypeWithAnnotatedGettersAndSetters
import springfox.documentation.schema.mixins.ConfiguredObjectMapperSupport
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.ServicePluginsSupport

import static java.util.Collections.*
import static springfox.documentation.schema.ResolvedTypes.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ApiModelPropertyPropertyBuilderSpec
    extends Specification
    implements ServicePluginsSupport,
        AlternateTypesSupport,
        ConfiguredObjectMapperSupport {
  BeanDescription beanDescription
  def descriptions = new DescriptionResolver(new MockEnvironment())

  def setup() {
    beanDescription = beanDescription(TypeWithAnnotatedGettersAndSetters)
  }

  def "Should all swagger documentation types"() {
    given:
    def sut = new ApiModelPropertyPropertyBuilder()

    expect:
    !sut.supports(SPRING_WEB)
    sut.supports(SWAGGER_12)
    sut.supports(SWAGGER_2)
  }

  def "ApiModelProperty annotated models get enriched with additional info given a bean property"() {
    given:
    ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder(descriptions)
    def properties = beanDescription.findProperties()
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        properties.find { it.name == property },
        new TypeResolver(),
        SWAGGER_12, new springfox.documentation.builders.PropertySpecificationBuilder())

    when:
    sut.apply(context)

    and:
    def enriched = context.getBuilder().build()

    then:
    enriched.allowableValues?.values == allowableValues
    enriched.isRequired() == required
    enriched.description == description
    enriched.readOnly == readOnly
    !enriched.isHidden()

    where:
    property        | required | description                | allowableValues | readOnly
    "intProp"       | true     | "int Property Field"       | null            | false
    "boolProp"      | false    | "bool Property Getter"     | null            | false
    "enumProp"      | true     | "enum Prop Getter value"   | ["ONE"]         | false
    "readOnlyProp"  | false    | "readOnly property getter" | null            | true
    "listOfStrings" | false    | "Some description"         | null            | false
    "interfaceProp" | true     | "interface Property Field" | null            | false
  }

  def "ApiModelProperty annotated models get enriched with additional info given an annotated element"() {
    given:
    ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder(descriptions)
    def properties = beanDescription.findProperties()
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(), new springfox.documentation.builders.PropertySpecificationBuilder(),
        properties.find { it.name == property }.getter.annotated,
        new TypeResolver(),
        SWAGGER_12)

    when:
    sut.apply(context)

    and:
    def enriched = context.getBuilder().build()

    then:
    enriched.allowableValues?.values == allowableValues
    enriched.isRequired() == (required == null ? false : required)
    enriched.description == description
    enriched.readOnly == (readOnly == null ? false : readOnly)
    !enriched.isHidden()

    where:
    property        | required | description                | allowableValues | readOnly
    "intProp"       | null     | null                       | null            | null
    "boolProp"      | false    | "bool Property Getter"     | null            | false
    "enumProp"      | true     | "enum Prop Getter value"   | ["ONE"]         | false
    "readOnlyProp"  | false    | "readOnly property getter" | null            | true
    "listOfStrings" | false    | "Some description"         | null            | false
    "interfaceProp" | true     | "interface Property Field" | null            | false
  }

  def "ApiModelProperties marked as hidden properties are respected"() {
    given:
    ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder(descriptions)
    def properties = beanDescription.findProperties()
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(), new springfox.documentation.builders.PropertySpecificationBuilder(),
        properties.find { it.name == property }.getter.annotated,
        new TypeResolver(),
        SWAGGER_12)

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
    property     | required | description | allowableValues
    "hiddenProp" | false    | ""          | null
  }

  def "Supports ApiModelProperty annotated models with dataType overrides"() {
    given:
    ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder(descriptions)
    def properties = beanDescription.findProperties()

    def resolver = new TypeResolver()
    ModelContext modelContext = inputParam(
        "0_0",
        "group",
        resolver.resolve(TypeWithAnnotatedGettersAndSetters),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        new DefaultGenericTypeNamingStrategy(),
        emptySet())
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    def context = new ModelPropertyContext(new ModelPropertyBuilder(), new springfox.documentation.builders.PropertySpecificationBuilder(),
        properties.find { it.name == property }.getter.annotated, resolver,
        SWAGGER_12)

    when:
    sut.apply(context)

    and:
    def enriched = context.getBuilder().build()
    enriched.updateModelRef(
        modelRefFactory(
            modelContext,
            new JacksonEnumTypeDeterminer(),
            typeNameExtractor))

    then:
    enriched.allowableValues?.values == null
    !enriched.isRequired()
    enriched.description == ""
    !enriched.isHidden()
    enriched.type.getErasedType() == dataType
    enriched.modelRef.type == modelRef

    where:
    property          | dataType | modelRef
    "validOverride"   | String   | "string"
    "invalidOverride" | Object   | "object"
  }

  def "Supports ApiModelProperty annotated models with dataType overrides but protects specific types"() {
    given:
    ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder(descriptions)
    def properties = beanDescription.findProperties()

    def resolver = new TypeResolver()
    ModelContext modelContext = inputParam(
        "0_0",
        "group",
        resolver.resolve(TypeWithAnnotatedGettersAndSetters),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        new DefaultGenericTypeNamingStrategy(),
        emptySet())
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    def context = new ModelPropertyContext(new ModelPropertyBuilder(), new springfox.documentation.builders.PropertySpecificationBuilder(),
        properties.find { it.name == property }.getter.annotated, resolver,
        SWAGGER_12)

    when:
    context.builder.type(resolver.resolve(dataType))

    and:
    sut.apply(context)

    and:
    def enriched = context.getBuilder().build()
    enriched.updateModelRef(
        modelRefFactory(
            modelContext,
            new JacksonEnumTypeDeterminer(),
            typeNameExtractor))

    then:
    enriched.allowableValues?.values == null
    !enriched.isRequired()
    enriched.description == ""
    !enriched.isHidden()
    enriched.type.getErasedType() == dataType
    enriched.modelRef.type == modelRef

    where:
    property          | dataType  | modelRef
    "validOverride"   | String    | "string"
    "invalidOverride" | LocalDate | "LocalDate"
  }

  BeanDescription beanDescription(Class<TypeWithAnnotatedGettersAndSetters> clazz) {
    def objectMapper = new ObjectMapper()
    objectMapper.getDeserializationConfig()
        .introspect(TypeFactory.defaultInstance().constructType(clazz))
  }
}
