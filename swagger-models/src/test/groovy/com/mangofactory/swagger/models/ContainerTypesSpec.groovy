package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
import com.mangofactory.swagger.models.dto.ModelRef
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.models.ResolvedTypes.responseTypeName

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

  @Unroll
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
      modelProperty.getType().dataType.reference == name
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.getAbsoluteType() == itemType

      asReturn.getName() == "ListsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().dataType.reference == name
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.getType().getAbsoluteType() == itemType

    where:
      property          | name    | itemType      | itemQualifiedType
      "complexTypes"    | "array" | 'ComplexType' | "com.mangofactory.swagger.models.ComplexType"
      "enums"           | "array" | "string"      | "com.mangofactory.swagger.models.ExampleEnum"
      "aliasOfIntegers" | "array" | "integer"     | "java.lang.Integer"
      "strings"         | "array" | "string"      | "java.lang.String"
      "objects"         | "array" | "object"      | "java.lang.Object"
  }

  @Unroll
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
      modelProperty.getType().dataType.reference == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.getType().getAbsoluteType() == itemType

      asReturn.getName() == "SetsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().getAbsoluteType() == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.getType().getAbsoluteType() == itemType

    where:
      property          | type    | itemType      | itemQualifiedType
      "complexTypes"    | "array" | 'ComplexType' | "com.mangofactory.swagger.models.ComplexType"
      "enums"           | "array" | "string"      | "com.mangofactory.swagger.models.ExampleEnum"
      "aliasOfIntegers" | "array" | "integer"     | "java.lang.Integer"
      "strings"         | "array" | "string"      | "java.lang.String"
      "objects"         | "array" | "object"      | "java.lang.Object"
  }

  @Unroll
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
      modelProperty.getType().dataType.reference == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.getType().getAbsoluteType() == itemType

      asReturn.getName() == "ArraysContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().dataType.reference == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.getType().getAbsoluteType() == itemType

    where:
      property          | type    | itemType      | itemQualifiedType
      "complexTypes"    | "array" | 'ComplexType' | "com.mangofactory.swagger.models.ComplexType"
      "enums"           | "array" | "string"      | "com.mangofactory.swagger.models.ExampleEnum"
      "aliasOfIntegers" | "array" | "integer"     | "java.lang.Integer"
      "strings"         | "array" | "string"      | "java.lang.String"
      "objects"         | "array" | "object"      | "java.lang.Object"
      "bytes"           | "array" | "string"      | "byte"
  }

  @Unroll
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
      modelProperty.getType().dataType.reference == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.getType().getAbsoluteType() == itemRef

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().dataType.reference == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.getType().getAbsoluteType() == itemRef

    where:
      property              | type    | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | "array" | "Entry«string,SimpleType»"   | "com.mangofactory.swagger.models.alternates.Entry"
      "stringToSimpleType"  | "array" | "Entry«string,SimpleType»"   | "com.mangofactory.swagger.models.alternates.Entry"
      "complexToSimpleType" | "array" | "Entry«Category,SimpleType»" | "com.mangofactory.swagger.models.alternates.Entry"
  }

  @Unroll
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
      modelProperty.getType().dataType.reference == type
      modelProperty.getItems()
      ModelRef item = modelProperty.getItems()
      item.getType().getAbsoluteType() == itemRef

      asReturn.getName() == "MapsContainer"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().dataType.reference == type
      retModelProperty.getItems()
      def retItem = retModelProperty.getItems()
      retItem.getType().getAbsoluteType() == itemRef

    where:
      property              | type    | itemRef                      | itemQualifiedType
      "enumToSimpleType"    | "array" | "Entry«string,SimpleType»"   | "com.mangofactory.swagger.models.alternates.Entry"
      "stringToSimpleType"  | "array" | "Entry«string,SimpleType»"   | "com.mangofactory.swagger.models.alternates.Entry"
      "complexToSimpleType" | "array" | "Entry«Category,SimpleType»" | "com.mangofactory.swagger.models.alternates.Entry"
  }
}
