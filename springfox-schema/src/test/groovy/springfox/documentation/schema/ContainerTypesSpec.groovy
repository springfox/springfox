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
import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.schema.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ContainerTypesSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "Model properties of type List, are inferred correctly"() {
    given:
      def sut = typeWithLists()
      List asInputContexts = modelProvider.modelsFor(inputParam(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(returnValue(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("ListsContainer")
      def asInput = asInputModels.get("ListsContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 6
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == name
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemType
      item.collection

      asReturnModels.containsKey("ListsContainer")
      def asReturn = asReturnModels.get("ListsContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 6
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == name
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "List"
      retItem.itemType == itemType
      retItem.collection

    where:
      property          | name      | itemType      | itemQualifiedType
      "complexTypes"    | List      | 'ComplexType' | "springfox.documentation.schema.ComplexType"
      "enums"           | List      | "string"      | "springfox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | List      | "int"         | "java.lang.Integer"
      "strings"         | ArrayList | "string"      | "java.lang.String"
      "objects"         | List      | "object"      | "java.lang.Object"
      "substituted"     | List      | "Substituted" | "springfox.documentation.schema.Substituted"
  }

  def "Model properties are inferred correctly"() {
    given:
      def sut = typeWithSets()
      List asInputContexts = modelProvider.modelsFor(inputParam(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(returnValue(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("SetsContainer")
      def asInput = asInputModels.get("SetsContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 5
      def modelProperty = asInput.getProperties().get(property)
      containerType(modelProperty.getType()) == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == type
      item.itemType == itemType
      item.collection

      asReturnModels.containsKey("SetsContainer")
      def asReturn = asReturnModels.get("SetsContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 5
      def retModelProperty = asReturn.getProperties().get(property)
      containerType(retModelProperty.type) == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == type
      retItem.itemType == itemType
      retItem.collection

    where:
      property          | type  | itemType      | itemQualifiedType
      "complexTypes"    | "Set" | 'ComplexType' | "springfox.documentation.schema.ComplexType"
      "enums"           | "Set" | "string"      | "springfox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | "Set" | "int"         | "java.lang.Integer"
      "strings"         | "Set" | "string"      | "java.lang.String"
      "objects"         | "Set" | "object"      | "java.lang.Object"
  }

  @Unroll
  def "Model properties of type Arrays are inferred correctly for #property"() {
    given:
      def sut = typeWithArrays()
      List asInputContexts = modelProvider.modelsFor(inputParam(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(returnValue(
          sut,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("ArraysContainer")
      def asInput = asInputModels.get("ArraysContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 10
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "Array"
      item.itemType == itemType
      item.collection

      asReturnModels.containsKey("ArraysContainer")
      def asReturn = asReturnModels.get("ArraysContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 10
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "Array"
      retItem.itemType == itemType
      retItem.collection

    where:
      property               | type          | itemType      | itemQualifiedType
      "complexTypes"         | ComplexType[] | 'ComplexType' | "springfox.documentation.schema.ComplexType"
      "enums"                | ExampleEnum[] | "string"      | "springfox.documentation.schema.ExampleEnum"
      "aliasOfIntegers"      | Integer[]     | "int"         | "java.lang.Integer"
      "strings"              | String[]      | "string"      | "java.lang.String"
      "objects"              | Object[]      | "object"      | "java.lang.Object"
      "bytes"                | byte[]        | "byte"        | "byte"
      "substituted"          | Substituted[] | "Substituted" | "springfox.documentation.schema.Substituted"
      "arrayOfArrayOfInts"   | int[][]       | "Array"       | "Array"
      "arrayOfListOfStrings" | List[]        | "List"        | "Array"
      "aliasOfIntegers"      | Integer[]     | "int"         | "java.lang.Integer"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      List asInputContexts = modelProvider.modelsFor(inputParam(
          sut,
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = modelProvider.modelsFor(returnValue(
          sut,
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("MapsContainer")
      def asInput = asInputModels.get("MapsContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 4
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

      asReturnModels.containsKey("MapsContainer")
      def asReturn = asReturnModels.get("MapsContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 4
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "List"
      retItem.itemType == itemRef
      retItem.collection

    where:
      property                       | type  | itemRef                      | itemQualifiedType
      "enumToSimpleType"             | List  | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "stringToSimpleType"           | List  | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "complexToSimpleType"          | List  | "Entry«Category,SimpleType»" | "springfox.documentation.schema.Entry"
      "mapOfmapOfStringToSimpleType" | List | "Entry«string,Map«string,SimpleType»»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = genericTypeOfMapsContainer()

      def modelContext = inputParam(
        sut,
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        ImmutableSet.builder().build())
      List asInputContexts = modelProvider.modelsFor(modelContext)
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    def returnContext = returnValue(
        sut,
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        ImmutableSet.builder().build())
      List asReturnContexts = modelProvider.modelsFor(returnContext)
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("MapsContainer")
      def asInput = asInputModels.get("MapsContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 4
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

      asReturnModels.containsKey("MapsContainer")
      def asReturn = asReturnModels.get("MapsContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 4
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "List"
      retItem.itemType == itemRef
      retItem.collection

    where:
      property                       | type   | itemRef                      | itemQualifiedType
      "enumToSimpleType"             | List   | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "stringToSimpleType"           | List   | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "complexToSimpleType"          | List   | "Entry«Category,SimpleType»" | "springfox.documentation.schema.Entry"
      "mapOfmapOfStringToSimpleType" | List   | "Entry«string,Map«string,SimpleType»»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host with default rules"() {
    given:
      def sut = genericTypeOfMapsContainer()

      def modelContext = inputParam(
          sut,
          SWAGGER_2,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      List asInputContexts = modelProvider.modelsFor(modelContext)
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

      def returnContext = returnValue(
          sut,
          SWAGGER_2,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      List asReturnContexts = modelProvider.modelsFor(returnContext)
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey("MapsContainer")
      def asInput = asInputModels.get("MapsContainer")
      asInput.getProperties().containsKey(property)
      asInput.getProperties().size() == 4
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == itemType
      item.itemType == itemRef
      !item.collection

      asReturnModels.containsKey("MapsContainer")
      def asReturn = asReturnModels.get("MapsContainer")
      asReturn.getProperties().containsKey(property)
      asReturn.getProperties().size() == 4
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == itemType
      retItem.itemType == itemRef
      !retItem.collection

    where:
      property                       | type   | itemRef                  | itemType
      "enumToSimpleType"             | Map    | "SimpleType"             | "Map«string,SimpleType»"
      "stringToSimpleType"           | Map    | "SimpleType"             | "Map«string,SimpleType»"
      "complexToSimpleType"          | Map    | "SimpleType"             | "Map«Category,SimpleType»"
      "mapOfmapOfStringToSimpleType" | Map    | "Map«string,SimpleType»" | "Map«string,Map«string,SimpleType»»"
  }
}
