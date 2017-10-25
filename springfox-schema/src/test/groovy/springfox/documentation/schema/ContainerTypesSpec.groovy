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

import com.google.common.base.Optional;
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
      Map<String, Model> models = new HashMap<String, Model>();

      Model asInput = modelProvider.modelFor(inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = modelProvider.modelFor(returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == name
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemType
      item.collection

    where:
      modelName          | property          | name      | itemType        | itemQualifiedType
      "ListsContainer"   | "complexTypes"    | List      | 'ComplexType'   | "springfox.documentation.schema.ComplexType"
      "ListsContainer"   | "enums"           | List      | "string"        | "springfox.documentation.schema.ExampleEnum"
      "ListsContainer"   | "aliasOfIntegers" | List      | "int"           | "java.lang.Integer"
      "ListsContainer"   | "strings"         | ArrayList | "string"        | "java.lang.String"
      "ListsContainer"   | "objects"         | List      | "object"        | "java.lang.Object"
      "ListsContainer"   | "substituted"     | List      | "Substituted"   | "springfox.documentation.schema.Substituted"
      "ListsContainer_1" | "complexTypes"    | List      | 'ComplexType_1' | "springfox.documentation.schema.ComplexType"
      "ListsContainer_1" | "enums"           | List      | "string"        | "springfox.documentation.schema.ExampleEnum"
      "ListsContainer_1" | "aliasOfIntegers" | List      | "int"           | "java.lang.Integer"
      "ListsContainer_1" | "strings"         | ArrayList | "string"        | "java.lang.String"
      "ListsContainer_1" | "objects"         | List      | "object"        | "java.lang.Object"
      "ListsContainer_1" | "substituted"     | List      | "Substituted_1" | "springfox.documentation.schema.Substituted"
  }

  def "Model properties are inferred correctly"() {
    given:
      def sut = typeWithSets()
      Map<String, Model> models = new HashMap<String, Model>();

      Model asInput = modelProvider.modelFor(inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = modelProvider.modelFor(returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      containerType(modelProperty.getType()) == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == type
      item.itemType == itemType
      item.collection

    where:
      modelName         |property           | type  | itemType        | itemQualifiedType
      "SetsContainer"   | "complexTypes"    | "Set" | 'ComplexType'   | "springfox.documentation.schema.ComplexType"
      "SetsContainer"   | "enums"           | "Set" | "string"        | "springfox.documentation.schema.ExampleEnum"
      "SetsContainer"   | "aliasOfIntegers" | "Set" | "int"           | "java.lang.Integer"
      "SetsContainer"   | "strings"         | "Set" | "string"        | "java.lang.String"
      "SetsContainer"   | "objects"         | "Set" | "object"        | "java.lang.Object"
      "SetsContainer_1" | "complexTypes"    | "Set" | 'ComplexType_1' | "springfox.documentation.schema.ComplexType"
      "SetsContainer_1" | "enums"           | "Set" | "string"        | "springfox.documentation.schema.ExampleEnum"
      "SetsContainer_1" | "aliasOfIntegers" | "Set" | "int"           | "java.lang.Integer"
      "SetsContainer_1" | "strings"         | "Set" | "string"        | "java.lang.String"
      "SetsContainer_1" | "objects"         | "Set" | "object"        | "java.lang.Object"
  }

  @Unroll
  def "Model properties of type Arrays are inferred correctly for #property"() {
    given:
      def sut = typeWithArrays()
      Map<String, Model> models = new HashMap<String, Model>();

      Model asInput = modelProvider.modelFor(inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = modelProvider.modelFor(returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "Array"
      item.itemType == itemType
      item.collection

    where:
      modelName           | property               | type          | itemType        | itemQualifiedType
      "ArraysContainer"   | "complexTypes"         | ComplexType[] | 'ComplexType'   | "springfox.documentation.schema.ComplexType"
      "ArraysContainer"   | "enums"                | ExampleEnum[] | "string"        | "springfox.documentation.schema.ExampleEnum"
      "ArraysContainer"   | "aliasOfIntegers"      | Integer[]     | "int"           | "java.lang.Integer"
      "ArraysContainer"   | "strings"              | String[]      | "string"        | "java.lang.String"
      "ArraysContainer"   | "objects"              | Object[]      | "object"        | "java.lang.Object"
      "ArraysContainer"   | "bytes"                | byte[]        | "byte"          | "byte"
      "ArraysContainer"   | "substituted"          | Substituted[] | "Substituted"   | "springfox.documentation.schema.Substituted"
      "ArraysContainer"   | "arrayOfArrayOfInts"   | int[][]       | "Array"         | "Array"
      "ArraysContainer"   | "arrayOfListOfStrings" | List[]        | "List"          | "Array"
      "ArraysContainer_1" | "complexTypes"         | ComplexType[] | 'ComplexType_1' | "springfox.documentation.schema.ComplexType"
      "ArraysContainer_1" | "enums"                | ExampleEnum[] | "string"        | "springfox.documentation.schema.ExampleEnum"
      "ArraysContainer_1" | "aliasOfIntegers"      | Integer[]     | "int"           | "java.lang.Integer"
      "ArraysContainer_1" | "strings"              | String[]      | "string"        | "java.lang.String"
      "ArraysContainer_1" | "objects"              | Object[]      | "object"        | "java.lang.Object"
      "ArraysContainer_1" | "bytes"                | byte[]        | "byte"          | "byte"
      "ArraysContainer_1" | "substituted"          | Substituted[] | "Substituted_1" | "springfox.documentation.schema.Substituted"
      "ArraysContainer_1" | "arrayOfArrayOfInts"   | int[][]       | "Array"         | "Array"
      "ArraysContainer_1" | "arrayOfListOfStrings" | List[]        | "List"          | "Array"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      Map<String, Model> models = new HashMap<String, Model>();
      
      Model asInput = modelProvider.modelFor(inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = modelProvider.modelFor(returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

    where:
      modelName         | property              | type  | itemRef                            | itemQualifiedType
      "MapsContainer"   | "enumToSimpleType"    | List  | "Entry«string,SimpleType»"         | "springfox.documentation.schema.Entry"
      "MapsContainer"   | "stringToSimpleType"  | List  | "Entry_1«string,SimpleType»"       | "springfox.documentation.schema.Entry"
      "MapsContainer"   | "complexToSimpleType" | List  | "Entry_2«Category,SimpleType»"     | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "enumToSimpleType"    | List  | "Entry_4«string,SimpleType_1»"     | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "stringToSimpleType"  | List  | "Entry_5«string,SimpleType_1»"     | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "complexToSimpleType" | List  | "Entry_6«Category_1,SimpleType_1»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = genericTypeOfMapsContainer()
      Map<String, Model> models = new HashMap<String, Model>();

      def modelContext = inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build())
      models = modelProvider.dependencies(modelContext)

      def returnContext = returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_12,
          alternateRulesWithWildcardMap(),
          namingStrategy,
          ImmutableSet.builder().build())
      models.putAll(modelProvider.dependencies(returnContext));

    expect:
      models.containsKey(modelName)
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == "List"
      item.itemType == itemRef
      item.collection

    where:
      modelName         | property                       | type | itemRef                                    | itemQualifiedType
      "MapsContainer"   | "enumToSimpleType"             | List | "Entry«string,SimpleType»"                 | "springfox.documentation.schema.Entry"
      "MapsContainer"   | "stringToSimpleType"           | List | "Entry_1«string,SimpleType»"               | "springfox.documentation.schema.Entry"
      "MapsContainer"   | "complexToSimpleType"          | List | "Entry_2«Category,SimpleType»"             | "springfox.documentation.schema.Entry"
      "MapsContainer"   | "mapOfmapOfStringToSimpleType" | List | "Entry_3«string,Map«string,SimpleType»»"   | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "enumToSimpleType"             | List | "Entry_4«string,SimpleType_1»"             | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "stringToSimpleType"           | List | "Entry_5«string,SimpleType_1»"             | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "complexToSimpleType"          | List | "Entry_6«Category_1,SimpleType_1»"         | "springfox.documentation.schema.Entry"
      "MapsContainer_1" | "mapOfmapOfStringToSimpleType" | List | "Entry_7«string,Map«string,SimpleType_1»»" | "springfox.documentation.schema.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host with default rules"() {
    given:
      def sut = genericTypeOfMapsContainer()

      def modelContext = inputParam("group",
          sut,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_2,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      Map<String, Model> models = modelProvider.dependencies(modelContext)

      def returnContext = returnValue("group",
          sut,
          Optional.absent(),
          SWAGGER_2,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      models.putAll(modelProvider.dependencies(returnContext))

    expect:
      models.containsKey(modelName)
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getModelRef()
      ModelRef item = modelProperty.getModelRef()
      item.type == itemType
      item.itemType == itemRef
      !item.collection

    where:
      modelName         | property                       | type   | itemRef                    | itemType
      "MapsContainer"   | "enumToSimpleType"             | Map    | "SimpleType"               | "Map«string,SimpleType»"
      "MapsContainer"   | "stringToSimpleType"           | Map    | "SimpleType"               | "Map«string,SimpleType»"
      "MapsContainer"   | "complexToSimpleType"          | Map    | "SimpleType"               | "Map«Category,SimpleType»"
      "MapsContainer"   | "mapOfmapOfStringToSimpleType" | Map    | "Map«string,SimpleType»"   | "Map«string,Map«string,SimpleType»»"
      "MapsContainer_1" | "enumToSimpleType"             | Map    | "SimpleType_1"             | "Map«string,SimpleType_1»"
      "MapsContainer_1" | "stringToSimpleType"           | Map    | "SimpleType_1"             | "Map«string,SimpleType_1»"
      "MapsContainer_1" | "complexToSimpleType"          | Map    | "SimpleType_1"             | "Map«Category_1,SimpleType_1»"
      "MapsContainer_1" | "mapOfmapOfStringToSimpleType" | Map    | "Map«string,SimpleType_1»" | "Map«string,Map«string,SimpleType_1»»"
  }
}
