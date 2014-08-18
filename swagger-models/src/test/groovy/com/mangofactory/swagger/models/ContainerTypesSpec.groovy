package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.model.Model
import com.wordnik.swagger.model.ModelRef
import org.springframework.http.HttpHeaders
import scala.Option
import spock.lang.Specification

import static com.mangofactory.swagger.models.ResolvedTypes.responseTypeName

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ContainerTypesSpec extends Specification {
  def "Response class for container types are inferred correctly"() {
    given:
    expect:
      responseTypeName(containerType) == name

    where:
      containerType                   | name
      genericListOfSimpleType()       | "List[SimpleType]"
      genericListOfInteger()          | "List"
      erasedList()                    | "List"
      genericSetOfSimpleType()        | "Set[SimpleType]"
      genericSetOfInteger()           | "Set"
      erasedSet()                     | "Set"
      genericClassWithGenericField()  | "GenericType«ResponseEntity«SimpleType»»"

  }

  def "Model properties of type List, are inferred correctly"() {
    given:
      def sut = typeWithLists()
      ModelProvider provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name() == "ListsContainer"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == name
      !modelProperty.get().items().isEmpty()
      ModelRef item = modelProperty.get().items().get()
      item.type() == itemType
      item.ref() == Option.apply(itemRef)
      item.qualifiedType() == Option.apply(itemQualifiedType)

      asReturn.name() == "ListsContainer"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == name
      !retModelProperty.get().items().isEmpty()
      def retItem = retModelProperty.get().items().get()
      retItem.type() == itemType
      retItem.ref() == Option.apply(itemRef)
      retItem.qualifiedType() == Option.apply(itemQualifiedType)

    where:
    property          | name                  | itemType        | itemRef            | itemQualifiedType
    "complexTypes"    | "List"                | null            | "ComplexType"      | "com.mangofactory.swagger.models.ComplexType"
    "enums"           | "List"                | "string"        | null               | "com.mangofactory.swagger.models.ExampleEnum"
    "aliasOfIntegers" | "List"                | "int"           | null               | "java.lang.Integer"
    "strings"         | "List"                | "string"        | null               | "java.lang.String"
    "objects"         | "List"                | "object"        | null               | "java.lang.Object"
  }


  def "Model properties of type Set, are inferred correctly"() {
    given:
    def sut = typeWithSets()
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
    Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name() == "SetsContainer"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      !modelProperty.get().items().isEmpty()
      ModelRef item = modelProperty.get().items().get()
      item.type() == itemType
      item.ref() == Option.apply(itemRef)
      item.qualifiedType() == Option.apply(itemQualifiedType)

      asReturn.name() == "SetsContainer"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      !retModelProperty.get().items().isEmpty()
      def retItem = retModelProperty.get().items().get()
      retItem.type() == itemType
      retItem.ref() == Option.apply(itemRef)
      retItem.qualifiedType() == Option.apply(itemQualifiedType)

    where:
    property          | type                  | itemType        | itemRef            | itemQualifiedType
    "complexTypes"    | "Set"                 | null            | "ComplexType"      | "com.mangofactory.swagger.models.ComplexType"
    "enums"           | "Set"                 | "string"        | null               | "com.mangofactory.swagger.models.ExampleEnum"
    "aliasOfIntegers" | "Set"                 | "int"           | null               | "java.lang.Integer"
    "strings"         | "Set"                 | "string"        | null               | "java.lang.String"
    "objects"         | "Set"                 | "object"        | null               | "java.lang.Object"
  }

  def "Model properties of type Arrays are inferred correctly"() {
    given:
    def sut = typeWithArrays()
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
    Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name() == "ArraysContainer"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      !modelProperty.get().items().isEmpty()
      ModelRef item = modelProperty.get().items().get()
      item.type() == itemType
      item.ref() == Option.apply(itemRef)
      item.qualifiedType() == Option.apply(itemQualifiedType)

      asReturn.name() == "ArraysContainer"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      !retModelProperty.get().items().isEmpty()
      def retItem = retModelProperty.get().items().get()
      retItem.type() == itemType
      retItem.ref() == Option.apply(itemRef)
      retItem.qualifiedType() == Option.apply(itemQualifiedType)

    where:
    property          | type       | itemType        | itemRef            | itemQualifiedType
    "complexTypes"    | "Array"    | null            | "ComplexType"      | "com.mangofactory.swagger.models.ComplexType"
    "enums"           | "Array"    | "string"        | null               | "com.mangofactory.swagger.models.ExampleEnum"
    "aliasOfIntegers" | "Array"    | "int"           | null               | "java.lang.Integer"
    "strings"         | "Array"    | "string"        | null               | "java.lang.String"
    "objects"         | "Array"    | "object"        | null               | "java.lang.Object"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name() == "MapsContainer"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      !modelProperty.get().items().isEmpty()
      ModelRef item = modelProperty.get().items().get()
      item.type() == null
      item.ref() == Option.apply(itemRef)
      item.qualifiedType() == Option.apply(itemQualifiedType)

      asReturn.name() == "MapsContainer"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      !retModelProperty.get().items().isEmpty()
      def retItem = retModelProperty.get().items().get()
      retItem.type() == null
      retItem.ref() == Option.apply(itemRef)
      retItem.qualifiedType() == Option.apply(itemQualifiedType)

    where:
      property              | type   | itemRef                     | itemQualifiedType
      "enumToSimpleType"    | "List" | "Entry«string,SimpleType»"  | "com.mangofactory.swagger.models.alternates.Entry"
      "stringToSimpleType"  | "List" | "Entry«string,SimpleType»"  | "com.mangofactory.swagger.models.alternates.Entry"
      "complexToSimpleType" | "List" | "Entry«Category,SimpleType»"| "com.mangofactory.swagger.models.alternates.Entry"
  }


  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = responseEntityWithDeepGenerics()
      def provider = defaultModelProvider()

      def modelContext = ModelContext.inputParam(sut)
      modelContext.seen(resolver.resolve(HttpHeaders.class))
      Model asInput = provider.dependencies(modelContext).get("MapsContainer")

      def returnContext = ModelContext.returnValue(sut)
      returnContext.seen(resolver.resolve(HttpHeaders.class))
      Model asReturn = provider.dependencies(returnContext).get("MapsContainer")

    expect:
      asInput.name() == "MapsContainer"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      !modelProperty.get().items().isEmpty()
      ModelRef item = modelProperty.get().items().get()
      item.type() == null
      item.ref() == Option.apply(itemRef)
      item.qualifiedType() == Option.apply(itemQualifiedType)

      asReturn.name() == "MapsContainer"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      !retModelProperty.get().items().isEmpty()
      def retItem = retModelProperty.get().items().get()
      retItem.type() == null
      retItem.ref() == Option.apply(itemRef)
      retItem.qualifiedType() == Option.apply(itemQualifiedType)

    where:
      property              | type   | itemRef                     | itemQualifiedType
      "enumToSimpleType"    | "List" | "Entry«string,SimpleType»"  | "com.mangofactory.swagger.models.alternates.Entry"
      "stringToSimpleType"  | "List" | "Entry«string,SimpleType»"  | "com.mangofactory.swagger.models.alternates.Entry"
      "complexToSimpleType" | "List" | "Entry«Category,SimpleType»"| "com.mangofactory.swagger.models.alternates.Entry"
  }

}
