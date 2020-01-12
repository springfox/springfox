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
package springfox.documentation.schema.property.property

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.SchemaSpecification
import springfox.documentation.schema.TypeWithAnnotatedGettersAndSetters
import springfox.documentation.schema.TypeWithGettersAndSetters
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.bean.BeanModelProperty
import springfox.documentation.service.AllowableListValues

import static java.util.Collections.*
import static springfox.documentation.schema.property.BeanPropertyDefinitions.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class BeanModelPropertySpec extends SchemaSpecification implements ModelPropertyLookupSupport {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()

  @Unroll
  def "Extracting information from resolved properties #methodName"() {
    given:
    Class typeToTest = TypeWithGettersAndSetters
    def modelContext = inputParam("0_0",
        "group",
        resolver.resolve(typeToTest),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def method = accessorMethod(typeToTest, methodName)
    def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

    ObjectMapper mapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
    String propName = name(propertyDefinition, true, namingStrategy, "")
    def sut = new BeanModelProperty(
        propName,
        method,
        new TypeResolver(),
        alternateTypeProvider(),
        propertyDefinition)


    expect:
    sut.propertyDescription() == null
    !sut.required
    sut.isReadOnly()
    typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
    sut.qualifiedTypeName() == qualifiedTypeName
    sut.allowableValues() == null


    where:
    methodName    | required | typeName  | qualifiedTypeName
    "getIntProp"  | true     | "int"     | "int"
    "isBoolProp"  | false    | "boolean" | "boolean"
    "setIntProp"  | true     | "int"     | "int"
    "setBoolProp" | false    | "boolean" | "boolean"
  }

  @Ignore("Fix this via the plugin manager")
  def "Extracting information from ApiModelProperty annotation"() {
    given:
    Class typeToTest = TypeWithAnnotatedGettersAndSetters
    def modelContext = inputParam("0_0",
        "group",
        resolver.resolve(typeToTest),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def method = accessorMethod(typeToTest, methodName)
    def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

    ObjectMapper mapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
    String propName = name(propertyDefinition, true, namingStrategy, "")
    def sut = new BeanModelProperty(
        propName,
        method,
        new TypeResolver(),
        alternateTypeProvider(),
        propertyDefinition)

    expect:
    sut.propertyDescription() == description
    sut.required == required
    !sut.isReadOnly()
    typeNameExtractor.typeName(modelContext) == typeName
    sut.qualifiedTypeName() == qualifiedTypeName

    if (allowableValues) {
      sut.allowableValues().getValues() == allowableValues.getValues()
      sut.allowableValues().getValueType() == allowableValues.getValueType()
    }

    where:
    methodName    | description              | required | allowableValues                                                            | typeName  | qualifiedTypeName
    "getIntProp"  | "int Property Field"     | true     | null                                                                       | "int"     | "int"
    "isBoolProp"  | "bool Property Getter"   | false    | null                                                                       | "boolean" | "boolean"
    "getEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(Stream.of("ONE", "TWO").collect(toList()), "LIST") | "string"  | "springfox.documentation.schema.ExampleEnum"
    "setIntProp"  | "int Property Field"     | true     | null                                                                       | "int"     | "int"
    "setBoolProp" | "bool Property Getter"   | false    | null                                                                       | "boolean" | "boolean"
    "setEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(Stream.of("ONE", "TWO").collect(toList()), "LIST") | "string"  | "springfox.documentation.schema.ExampleEnum"
  }

  def "Respects JsonGetter annotations"() {

    given:
    Class typeToTest = typeForTestingJsonGetterAnnotation()
    def modelContext = inputParam("0_0",
        "group",
        resolver.resolve(typeToTest),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def method = accessorMethod(typeToTest, methodName)
    def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

    ObjectMapper mapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
    String propName = name(propertyDefinition, true, namingStrategy, "")
    def sut = new BeanModelProperty(
        propName,
        method,
        new TypeResolver(),
        alternateTypeProvider(),
        propertyDefinition)

    expect:
    typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
    sut.qualifiedTypeName() == qualifiedTypeName
    sut.allowableValues() == null

    where:
    methodName || typeName | qualifiedTypeName
    "value1"   || "string" | "java.lang.String"
  }
}
