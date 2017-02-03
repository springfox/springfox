/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

import com.google.common.collect.ImmutableSet
import spock.lang.Ignore
import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class SimpleTypeSpec extends SchemaSpecification {
  def namingStrategy = new CodeGenGenericTypeNamingStrategy()
  @Unroll
  def "simple type [#qualifiedType] is rendered as [#type]"() {
    given:
      List asInputContexts = modelProvider.modelsFor(
          inputParam(
              simpleType(),
              SWAGGER_12,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(
          returnValue(
              simpleType(),
              SWAGGER_12,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.size() == 1
      asInputModels.containsKey("SimpleType")
      def asInput = asInputModels.get("SimpleType")
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.modelRef
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturnContexts.size() == 1
      asReturnModels.containsKey("SimpleType")
      def asReturn = asReturnModels.get("SimpleType")
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      def retItem = retModelProperty.modelRef
      retItem.type == Types.typeNameFor(type)
      !retItem.collection
      retItem.itemType == null

    where:
      property          | type    | qualifiedType
      "aString"         | String  | "java.lang.String"
      "aByte"           | byte    | "byte"
      "aBoolean"        | boolean | "boolean"
      "aShort"          | short   | "int"
      "anInt"           | int     | "int"
      "aLong"           | long    | "long"
      "aFloat"          | float   | "float"
      "aDouble"         | double  | "double"
      "anObjectByte"    | Byte    | "java.lang.Byte"
      "anObjectBoolean" | Boolean | "java.lang.Boolean"
      "anObjectShort"   | Short   | "java.lang.Short"
      "anObjectInt"     | Integer | "java.lang.Integer"
      "anObjectLong"    | Long    | "java.lang.Long"
      "anObjectFloat"   | Float   | "java.lang.Float"
      "anObjectDouble"  | Double  | "java.lang.Double"
      "currency"        | Currency| "java.util.Currency"
      "uuid"            | UUID    | "java.util.UUID"
      "aDate"           | Date    | "java.util.Date"
      "aSqlDate"        | java.sql.Date   | "java.sql.Date"
  }

  @Ignore
  def "type with constructor all properties are inferred"() {
    given:
      List asInputContexts = modelProvider.modelsFor(
          inputParam(
              typeWithConstructor(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
     Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
    
     List asReturnContexts = modelProvider.modelsFor(
          returnValue(
              typeWithConstructor(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
     Map asReturnModels = asReturnContexts.collectEntries{
         [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.size() == 1
      asInputModels.containsKey("TypeWithConstructor")
      def asInput = asInputModels.get("TypeWithConstructor")
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType().erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.getModelRef()
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturnContexts.size() == 1
      asReturnModels.containsKey("TypeWithConstructor")
      def asReturn = asReturnModels.get("TypeWithConstructor")
      !asReturn.getProperties().containsKey(property)

    where:
      property      | type     | qualifiedType
      "stringValue" | String   | "java.lang.String"
  }

  def "Types with properties aliased using JsonProperty annotation"() {
    given:
      List asInputContexts = modelProvider.modelsFor(
          inputParam(
              typeWithJsonPropertyAnnotation(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(
          returnValue(
              typeWithJsonPropertyAnnotation(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};  

    expect:
      asInputModels.size() == 1
      asInputModels.containsKey("TypeWithJsonProperty")
      def asInput = asInputModels.get("TypeWithJsonProperty")
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.getModelRef()
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturnContexts.size() == 1
      asReturnModels.containsKey("TypeWithJsonProperty")
      def asReturn = asReturnModels.get("TypeWithJsonProperty")
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      def retItem = retModelProperty.getModelRef()
      retItem.type == Types.typeNameFor(type)
      !retItem.collection
      retItem.itemType == null

    where:
      property                    | type     | qualifiedType
      "some_custom_odd_ball_name" | String   | "java.lang.String"
  }
}
