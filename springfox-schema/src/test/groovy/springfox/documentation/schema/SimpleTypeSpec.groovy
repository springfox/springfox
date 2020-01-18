/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Unroll

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class SimpleTypeSpec extends SchemaSpecification implements ModelTestingSupport {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new CodeGenGenericTypeNamingStrategy()
  
  @Unroll
  def "simple type is rendered as [#type]"() {
    given:
    ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(
        returnValue("0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "SimpleType"
    asInput.getCompound().isPresent()
    assertScalarPropertySpecification(asInput.getCompound().get(), property, type)

    asReturn.getName() == "SimpleType"
    asReturn.getCompound().isPresent()
    assertScalarPropertySpecification(asReturn.getCompound().get(), property, type)

    where:
    property          | type
    "aString"         | ScalarType.STRING
    "aByte"           | ScalarType.BYTE
    "aBoolean"        | ScalarType.BOOLEAN
    "aShort"          | ScalarType.INTEGER
    "anInt"           | ScalarType.INTEGER
    "aLong"           | ScalarType.LONG
    "aFloat"          | ScalarType.FLOAT
    "aDouble"         | ScalarType.DOUBLE
    "anObjectByte"    | ScalarType.BYTE
    "anObjectBoolean" | ScalarType.BOOLEAN
    "anObjectShort"   | ScalarType.INTEGER
    "anObjectInt"     | ScalarType.INTEGER
    "anObjectLong"    | ScalarType.LONG
    "anObjectFloat"   | ScalarType.FLOAT
    "anObjectDouble"  | ScalarType.DOUBLE
    "uuid"            | ScalarType.UUID
    "currency"        | ScalarType.CURRENCY
    "aDate"           | ScalarType.DATE
    "aSqlDate"        | ScalarType.DATE_TIME
  }


  def "Types with properties aliased using JsonProperty annotation"() {
    given:
    ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            new HashSet<>(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "TypeWithJsonProperty"
    asInput.getCompound().isPresent()
    assertScalarPropertySpecification(asInput.getCompound().get(), property, type)

    asReturn.getName() == "TypeWithJsonProperty"
    asReturn.getCompound().isPresent()
    assertScalarPropertySpecification(asReturn.getCompound().get(), property, type)

    where:
    property                    | type
    "some_custom_odd_ball_name" | ScalarType.STRING
  }

  @Unroll
  def "(Deprecated) simple type [#qualifiedType] is rendered as [#type]"() {
    given:
    Model asInput = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    Model asReturn = modelProvider.modelFor(
        returnValue("0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "SimpleType"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    def item = modelProperty.modelRef
    item.type == Types.typeNameFor(type)
    !item.collection
    item.itemType == null

    asReturn.getName() == "SimpleType"
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getQualifiedType() == qualifiedType
    def retItem = retModelProperty.modelRef
    retItem.type == Types.typeNameFor(type)
    !retItem.collection
    retItem.itemType == null

    where:
    property          | type          | qualifiedType
    "aString"         | String        | "java.lang.String"
    "aByte"           | byte          | "byte"
    "aBoolean"        | boolean       | "boolean"
    "aShort"          | short         | "int"
    "anInt"           | int           | "int"
    "aLong"           | long          | "long"
    "aFloat"          | float         | "float"
    "aDouble"         | double        | "double"
    "anObjectByte"    | Byte          | "java.lang.Byte"
    "anObjectBoolean" | Boolean       | "java.lang.Boolean"
    "anObjectShort"   | Short         | "java.lang.Short"
    "anObjectInt"     | Integer       | "java.lang.Integer"
    "anObjectLong"    | Long          | "java.lang.Long"
    "anObjectFloat"   | Float         | "java.lang.Float"
    "anObjectDouble"  | Double        | "java.lang.Double"
    "currency"        | Currency      | "java.util.Currency"
    "uuid"            | UUID          | "java.util.UUID"
    "aDate"           | Date          | "java.util.Date"
    "aSqlDate"        | java.sql.Date | "java.sql.Date"
  }

  def "(Deprecated) Types with properties aliased using JsonProperty annotation"() {
    given:
    Model asInput = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            new HashSet<>(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    Model asReturn = modelProvider.modelFor(
        returnValue(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "TypeWithJsonProperty"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    def item = modelProperty.getModelRef()
    item.type == Types.typeNameFor(type)
    !item.collection
    item.itemType == null

    asReturn.getName() == "TypeWithJsonProperty"
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getQualifiedType() == qualifiedType
    def retItem = retModelProperty.getModelRef()
    retItem.type == Types.typeNameFor(type)
    !retItem.collection
    retItem.itemType == null

    where:
    property                    | type   | qualifiedType
    "some_custom_odd_ball_name" | String | "java.lang.String"
  }
}
