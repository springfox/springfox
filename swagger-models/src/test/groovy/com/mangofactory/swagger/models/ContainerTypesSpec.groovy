package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.ModelImpl
import com.wordnik.swagger.models.properties.ArrayProperty
import com.wordnik.swagger.models.properties.IntegerProperty
import com.wordnik.swagger.models.properties.RefProperty
import com.wordnik.swagger.models.properties.StringProperty
import org.springframework.http.HttpHeaders
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
      genericListOfSimpleType()       | "List«SimpleType»"
      genericListOfInteger()          | "List«int»"
      erasedList()                    | "List"
      genericSetOfSimpleType()        | "Set«SimpleType»"
      genericSetOfInteger()           | "Set«int»"
      erasedSet()                     | "Set"
  }

  def "Model properties of type List, are inferred correctly"() {
    given:
      def sut = typeWithLists()
      ModelProvider provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      ModelImpl asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name == "ListsContainer"
      asInput.properties."$property" != null
      ArrayProperty modelProperty = asInput.properties.get(property)
      modelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

      asReturn.name == "ListsContainer"
      asInput.properties."$property" != null
      def retModelProperty = asReturn.properties.get(property)
      retModelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

    where:
      property          | itemClass       | itemType
      "complexTypes"    | RefProperty     | "ComplexType"
      "enums"           | StringProperty  | "string"
      "aliasOfIntegers" | IntegerProperty | "integer"
      "strings"         | StringProperty  | "string"
      "objects"         | ObjectProperty  | "object"
  }


  def "Model properties of type Set, are inferred correctly"() {
    given:
      def sut = typeWithSets()
      ModelProvider provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      ModelImpl asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name == "SetsContainer"
      asInput.properties."$property" != null
      ArrayProperty modelProperty = asInput.properties.get(property)
      modelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

      asReturn.name == "SetsContainer"
      asInput.properties."$property" != null
      def retModelProperty = asReturn.properties.get(property)
      retModelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

    where:
      property          | itemClass       | itemType
      "complexTypes"    | RefProperty     | "ComplexType"
      "enums"           | StringProperty  | "string"
      "aliasOfIntegers" | IntegerProperty | "integer"
      "strings"         | StringProperty  | "string"
      "objects"         | ObjectProperty  | "object"
  }

  def "Model properties of type Arrays, are inferred correctly"() {
    given:
      def sut = typeWithArrays()
      ModelProvider provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      ModelImpl asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name == "ArraysContainer"
      asInput.properties."$property" != null
      ArrayProperty modelProperty = asInput.properties.get(property)
      modelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

      asReturn.name == "ArraysContainer"
      asInput.properties."$property" != null
      def retModelProperty = asReturn.properties.get(property)
      retModelProperty.type == "array"
      assert modelProperty.items.class == itemClass
      if (modelProperty.items.class == RefProperty) {
        assert itemType == ((RefProperty) modelProperty.items).$ref
      } else {
        assert itemType == modelProperty.items.type
      }

    where:
      property          | itemClass       | itemType
      "complexTypes"    | RefProperty     | "ComplexType"
      "enums"           | StringProperty  | "string"
      "aliasOfIntegers" | IntegerProperty | "integer"
      "strings"         | StringProperty  | "string"
      "objects"         | ObjectProperty  | "object"
  }

  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      def provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(ModelContext.inputParam(sut)).get()
      ModelImpl asReturn = provider.modelFor(ModelContext.returnValue(sut)).get()

    expect:
      asInput.name == "MapsContainer"
      assert asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      assert modelProperty.class == RefProperty
      assert ((RefProperty) modelProperty).$ref == ref

      asReturn.name == "MapsContainer"
      assert asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      assert retModelProperty.class == RefProperty
      assert ((RefProperty) retModelProperty).$ref == ref

    where:
      property              | ref
      "enumToSimpleType"    | "Map«string,SimpleType»"
      "stringToSimpleType"  | "Map«string,SimpleType»"
      "complexToSimpleType" | "Map«Category,SimpleType»"

  }

  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = responseEntityWithDeepGenerics()
      def provider = defaultModelProvider()

      def modelContext = ModelContext.inputParam(sut)
      modelContext.seen(resolver.resolve(HttpHeaders.class))
      ModelImpl asInput = provider.dependencies(modelContext).get("MapsContainer")

      def returnContext = ModelContext.returnValue(sut)
      returnContext.seen(resolver.resolve(HttpHeaders.class))
      ModelImpl asReturn = provider.dependencies(returnContext).get("MapsContainer")

    expect:
      asInput.name == "MapsContainer"
      assert asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      assert modelProperty.class == RefProperty
      assert ((RefProperty) modelProperty).$ref == ref

      asReturn.name == "MapsContainer"
      assert asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      assert retModelProperty.class == RefProperty
      assert ((RefProperty) retModelProperty).$ref == ref

    where:
      property              | ref
      "enumToSimpleType"    | "Map«string,SimpleType»"
      "stringToSimpleType"  | "Map«string,SimpleType»"
      "complexToSimpleType" | "Map«Category,SimpleType»"

  }

}
