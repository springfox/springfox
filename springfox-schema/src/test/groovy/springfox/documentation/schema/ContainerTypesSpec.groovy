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

package springfox.documentation.schema

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
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()

    expect:
      asInput.getName() == "ListsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == name
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemType
      item.collection

      asReturn.getName() == "ListsContainer"
      asReturn.getProperties().containsKey(property)
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
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()

    expect:
      asInput.getName() == "SetsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      containerType(modelProperty.getType()) == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == type
      item.itemType == itemType
      item.collection

      asReturn.getName() == "SetsContainer"
      asReturn.getProperties().containsKey(property)
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
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider(), namingStrategy)).get()

    expect:
      asInput.getName() == "ArraysContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "Array"
      item.itemType == itemType
      item.collection

      asReturn.getName() == "ArraysContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "Array"
      retItem.itemType == itemType
      retItem.collection

    where:
      property          | type          | itemType      | itemQualifiedType
      "complexTypes"    | ComplexType[] | 'ComplexType' | "springfox.documentation.schema.ComplexType"
      "enums"           | ExampleEnum[] | "string"      | "springfox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | Integer[]     | "int"         | "java.lang.Integer"
      "strings"         | String[]      | "string"      | "java.lang.String"
      "objects"         | Object[]      | "object"      | "java.lang.Object"
      "bytes"           | byte[]        | "byte"        | "byte"
      "substituted"     | Substituted[] | "Substituted" | "springfox.documentation.schema.Substituted"
      "arrayOfArrayOfInts"| int[][]     | "Array"       | "Array"
      "arrayOfListOfStrings"| List[]    | "List"        | "Array"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateRulesWithWildcardMap(), namingStrategy)).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateRulesWithWildcardMap(), namingStrategy)).get()

    expect:
      asInput.getName() == "MapsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "List"
      retItem.itemType == itemRef
      retItem.collection

    where:
      property              | type  | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "stringToSimpleType"  | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "complexToSimpleType" | List | "Entry«Category,SimpleType»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = genericTypeOfMapsContainer()

      def modelContext = inputParam(sut, SWAGGER_12, alternateRulesWithWildcardMap(), namingStrategy)
      Model asInput = modelProvider.dependencies(modelContext).get("MapsContainer")

      def returnContext = returnValue(sut, SWAGGER_12, alternateRulesWithWildcardMap(), namingStrategy)
      Model asReturn = modelProvider.dependencies(returnContext).get("MapsContainer")

    expect:
      asInput.getName() == "MapsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getModelRef()
      def retItem = retModelProperty.getModelRef()
      retItem.type == "List"
      retItem.itemType == itemRef
      retItem.collection

    where:
      property              | type   | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "stringToSimpleType"  | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
      "complexToSimpleType" | List | "Entry«Category,SimpleType»" | "springfox.documentation.schema.Entry"
      "mapOfmapOfStringToSimpleType" | List | "Entry«string,Map«string,SimpleType»»" | "springfox.documentation.schema.Entry"
  }
}
