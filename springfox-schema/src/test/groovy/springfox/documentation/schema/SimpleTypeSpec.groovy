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
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Unroll

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class SimpleTypeSpec extends SchemaSpecification {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new CodeGenGenericTypeNamingStrategy()
  
  @Unroll
  def "simple type [#qualifiedType] is rendered as [#type]"() {
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

  @Ignore
  def "type with constructor all properties are inferred"() {
    given:
    Model asInput = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithConstructor()),
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
            resolver.resolve(typeWithConstructor()),
            Optional.empty(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "TypeWithConstructor"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.getType().erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    def item = modelProperty.getModelRef()
    item.type == Types.typeNameFor(type)
    !item.collection
    item.itemType == null

    asReturn.getName() == "TypeWithConstructor"
    !asReturn.getProperties().containsKey(property)

    where:
    property      | type   | qualifiedType
    "stringValue" | String | "java.lang.String"
  }

  def "Types with properties aliased using JsonProperty annotation"() {
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

  @Ignore
  def "(Deprecated) type with constructor all properties are inferred"() {
    given:
    Model asInput = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithConstructor()),
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
            resolver.resolve(typeWithConstructor()),
            Optional.empty(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "TypeWithConstructor"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.getType().erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    def item = modelProperty.getModelRef()
    item.type == Types.typeNameFor(type)
    !item.collection
    item.itemType == null

    asReturn.getName() == "TypeWithConstructor"
    !asReturn.getProperties().containsKey(property)

    where:
    property      | type   | qualifiedType
    "stringValue" | String | "java.lang.String"
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
