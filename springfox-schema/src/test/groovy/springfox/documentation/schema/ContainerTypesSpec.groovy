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
import static springfox.documentation.schema.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ContainerTypesSpec extends SchemaSpecification {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "Model properties of type List, are inferred correctly"() {
    given:
    def sut = resolver.resolve(typeWithLists())
    Model asInput = modelProvider.modelFor(inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()
    Model asReturn = modelProvider.modelFor(returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

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
    def sut = resolver.resolve(typeWithSets())
    Model asInput = modelProvider.modelFor(inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    Model asReturn = modelProvider.modelFor(returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

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
    def sut = resolver.resolve(typeWithArrays())
    Model asInput = modelProvider.modelFor(inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    Model asReturn = modelProvider.modelFor(returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

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
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
    def sut = resolver.resolve(mapsContainer())
    Model asInput = modelProvider.modelFor(inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        emptySet())).get()

    Model asReturn = modelProvider.modelFor(returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        emptySet())).get()

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
    property              | type | itemRef                      | itemQualifiedType
    "enumToSimpleType"    | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
    "stringToSimpleType"  | List | "Entry«string,SimpleType»"   | "springfox.documentation.schema.Entry"
    "complexToSimpleType" | List | "Entry«Category,SimpleType»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
    def sut = genericTypeOfMapsContainer()

    def modelContext = inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        emptySet())

    List<Model> asInputModels = new ArrayList(modelProvider.dependencies(modelContext).values())

    def returnContext = returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_12,
        alternateRulesWithWildcardMap(),
        namingStrategy,
        emptySet())

    List<Model> asReturnModels = new ArrayList(modelProvider.dependencies(returnContext).values())

    expect:
    Model asInput = null
    for (int i = 0; i < asInputModels.size(); i++) {
      if (asInputModels.get(i).getName().equals("MapsContainer")) {
        asInput = asInputModels.get(i)
        break
      }
    }

    asInput != null
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getModelRef()
    ModelRef item = modelProperty.getModelRef()
    item.type == "List"
    item.itemType == itemRef
    item.collection

    Model asReturn = null
    for (int i = 0; i < asReturnModels.size(); i++) {
      if (asReturnModels.get(i).getName().equals("MapsContainer")) {
        asReturn = asReturnModels.get(i)
        break
      }
    }

    asReturn != null
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
    property                       | type | itemRef                                | itemQualifiedType
    "enumToSimpleType"             | List | "Entry«string,SimpleType»"             | "springfox.documentation.schema.Entry"
    "stringToSimpleType"           | List | "Entry«string,SimpleType»"             | "springfox.documentation.schema.Entry"
    "complexToSimpleType"          | List | "Entry«Category,SimpleType»"           | "springfox.documentation.schema.Entry"
    "mapOfmapOfStringToSimpleType" | List | "Entry«string,Map«string,SimpleType»»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host with default rules"() {
    given:
    def sut = genericTypeOfMapsContainer()

    def modelContext = inputParam("0_0",
        "group",
        sut,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_2,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())

    List<Model> asInputModels = new ArrayList(modelProvider.dependencies(modelContext).values())

    def returnContext = returnValue("0_0",
        "group",
        sut,
        Optional.empty(),
        SWAGGER_2,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())

    List<Model> asReturnModels = new ArrayList(modelProvider.dependencies(returnContext).values())

    expect:
    Model asInput = null
    for (int i = 0; i < asInputModels.size(); i++) {
      if (asInputModels.get(i).getName().equals("MapsContainer")) {
        asInput = asInputModels.get(i)
        break
      }
    }

    asInput != null
    asInput.getName() == "MapsContainer"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getModelRef()
    ModelRef item = modelProperty.getModelRef()
    item.type == itemType
    item.itemType == itemRef
    !item.collection

    Model asReturn = null
    for (int i = 0; i < asReturnModels.size(); i++) {
      if (asReturnModels.get(i).getName().equals("MapsContainer")) {
        asReturn = asReturnModels.get(i)
        break
      }
    }

    asReturn != null
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getModelRef()
    def retItem = retModelProperty.getModelRef()
    retItem.type == itemType
    retItem.itemType == itemRef
    !retItem.collection

    where:
    property                       | type | itemRef                  | itemType
    "enumToSimpleType"             | Map  | "SimpleType"             | "Map«string,SimpleType»"
    "stringToSimpleType"           | Map  | "SimpleType"             | "Map«string,SimpleType»"
    "complexToSimpleType"          | Map  | "SimpleType"             | "Map«Category,SimpleType»"
    "mapOfmapOfStringToSimpleType" | Map  | "Map«string,SimpleType»" | "Map«string,Map«string,SimpleType»»"
  }
}
