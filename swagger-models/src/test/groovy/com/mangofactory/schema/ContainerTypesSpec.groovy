package com.mangofactory.schema
import com.mangofactory.service.model.Model
import com.mangofactory.service.model.ModelRef
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.schema.Collections.*
import static com.mangofactory.schema.ResolvedTypes.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ContainerTypesSpec extends Specification {
  def "Response class for container types are inferred correctly"() {
    given:
    expect:
      responseTypeName(containerType) == name

    where:
      containerType                  | name
      genericListOfSimpleType()      | "List[SimpleType]"
      genericListOfInteger()         | "List[int]"
      erasedList()                   | "List"
      genericSetOfSimpleType()       | "Set[SimpleType]"
      genericSetOfInteger()          | "Set[int]"
      erasedSet()                    | "Set"
      genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"

  }

  def "Model properties of type List, are inferred correctly"() {
    given:
      def sut = typeWithLists()
      ModelProvider provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.getName() == "ListsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.typeName() == name
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.type == itemType

      asReturn.getName() == "ListsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == name
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.type == itemType

    where:
      property          | name   | itemType      | itemQualifiedType
      "complexTypes"    | "List" | 'ComplexType' | "com.mangofactory.schema.ComplexType"
      "enums"           | "List" | "string"      | "com.mangofactory.schema.ExampleEnum"
      "aliasOfIntegers" | "List" | "int"         | "java.lang.Integer"
      "strings"         | "List" | "string"      | "java.lang.String"
      "objects"         | "List" | "object"      | "java.lang.Object"
  }

  def "Model properties of type [#type], are inferred correctly"() {
    given:
      def sut = typeWithSets()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.getName() == "SetsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      containerType(modelProperty.getType()) == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.type == itemType

      asReturn.getName() == "SetsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      containerType(retModelProperty.type) == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.type == itemType

    where:
      property          | type  | itemType      | itemQualifiedType
      "complexTypes"    | "Set" | 'ComplexType' | "com.mangofactory.schema.ComplexType"
      "enums"           | "Set" | "string"      | "com.mangofactory.schema.ExampleEnum"
      "aliasOfIntegers" | "Set" | "int"         | "java.lang.Integer"
      "strings"         | "Set" | "string"      | "java.lang.String"
      "objects"         | "Set" | "object"      | "java.lang.Object"
  }

  def "Model properties of type Arrays are inferred correctly"() {
    given:
      def sut = typeWithArrays()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.getName() == "ArraysContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.typeName() == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.type == itemType

      asReturn.getName() == "ArraysContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.type == itemType

    where:
      property          | type    | itemType      | itemQualifiedType
      "complexTypes"    | "Array" | 'ComplexType' | "com.mangofactory.schema.ComplexType"
      "enums"           | "Array" | "string"      | "com.mangofactory.schema.ExampleEnum"
      "aliasOfIntegers" | "Array" | "int"         | "java.lang.Integer"
      "strings"         | "Array" | "string"      | "java.lang.String"
      "objects"         | "Array" | "object"      | "java.lang.Object"
      "bytes"           | "Array" | "byte"        | "byte"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.getName() == "MapsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.typeName() == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.type == itemRef

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.type == itemRef

    where:
      property              | type   | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | "List" | "Entry«string,SimpleType»"   | "com.mangofactory.schema.alternates.Entry"
      "stringToSimpleType"  | "List" | "Entry«string,SimpleType»"   | "com.mangofactory.schema.alternates.Entry"
      "complexToSimpleType" | "List" | "Entry«Category,SimpleType»" | "com.mangofactory.schema.alternates.Entry"
  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = genericTypeOfMapsContainer()
      def provider = defaultModelProvider()

      def modelContext = ModelContext.inputParam(sut)
      Model asInput = provider.dependencies(modelContext).get("MapsContainer")

      def returnContext = ModelContext.returnValue(sut)
      Model asReturn = provider.dependencies(returnContext).get("MapsContainer")

    expect:
      asInput.getName() == "MapsContainer"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.typeName() == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.type == itemRef

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.type == itemRef

    where:
      property              | type   | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | "List" | "Entry«string,SimpleType»"   | "com.mangofactory.schema.alternates.Entry"
      "stringToSimpleType"  | "List" | "Entry«string,SimpleType»"   | "com.mangofactory.schema.alternates.Entry"
      "complexToSimpleType" | "List" | "Entry«Category,SimpleType»" | "com.mangofactory.schema.alternates.Entry"
  }
}
