package springdox.documentation.schema

import spock.lang.Ignore
import springdox.documentation.schema.mixins.TypesForTestingSupport

import static springdox.documentation.schema.Collections.*
import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ContainerTypesSpec extends SchemaSpecification {
  def "Model properties of type List, are inferred correctly"() {
    given:
      def sut = typeWithLists()
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider())).get()

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
      "complexTypes"    | List      | 'ComplexType' | "springdox.documentation.schema.ComplexType"
      "enums"           | List      | "string"      | "springdox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | List      | "int"         | "java.lang.Integer"
      "strings"         | ArrayList | "string"      | "java.lang.String"
      "objects"         | List      | "object"      | "java.lang.Object"
  }

  def "Model properties are inferred correctly"() {
    given:
      def sut = typeWithSets()
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider())).get()

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
      "complexTypes"    | "Set" | 'ComplexType' | "springdox.documentation.schema.ComplexType"
      "enums"           | "Set" | "string"      | "springdox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | "Set" | "int"         | "java.lang.Integer"
      "strings"         | "Set" | "string"      | "java.lang.String"
      "objects"         | "Set" | "object"      | "java.lang.Object"
  }

  def "Model properties of type Arrays are inferred correctly"() {
    given:
      def sut = typeWithArrays()
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider())).get()

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
      "complexTypes"    | ComplexType[] | 'ComplexType' | "springdox.documentation.schema.ComplexType"
      "enums"           | ExampleEnum[] | "string"      | "springdox.documentation.schema.ExampleEnum"
      "aliasOfIntegers" | Integer[]     | "int"         | "java.lang.Integer"
      "strings"         | String[]      | "string"      | "java.lang.String"
      "objects"         | Object[]      | "object"      | "java.lang.Object"
      "bytes"           | byte[]        | "byte"        | "byte"
  }

  @Ignore("Should move this to the swagger 1.2 module")
  def "Model properties of type Map are inferred correctly"() {
    given:
      def sut = mapsContainer()
      Model asInput = modelProvider.modelFor(inputParam(sut, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(sut, SWAGGER_12, alternateTypeProvider())).get()

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
      "enumToSimpleType"    | List | "Entry«string,SimpleType»"   | "springdox.documentation.schema.Entry"
      "stringToSimpleType"  | List | "Entry«string,SimpleType»"   | "springdox.documentation.schema.Entry"
      "complexToSimpleType" | List | "Entry«Category,SimpleType»" | "springdox.documentation.schema.Entry"
  }

  @Ignore("Should move this to the swagger 1.2 module")
  def "Model properties of type Map are inferred correctly on generic host"() {
    given:
      def sut = genericTypeOfMapsContainer()

      def modelContext = inputParam(sut, SWAGGER_12, alternateTypeProvider())
      Model asInput = modelProvider.dependencies(modelContext).get("MapsContainer")

      def returnContext = returnValue(sut, SWAGGER_12, alternateTypeProvider())
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
      "enumToSimpleType"    | List | "Entry«string,SimpleType»"   | "springdox.documentation.schema.Entry"
      "stringToSimpleType"  | List | "Entry«string,SimpleType»"   | "springdox.documentation.schema.Entry"
      "complexToSimpleType" | List | "Entry«Category,SimpleType»" | "springdox.documentation.schema.Entry"
  }
}
